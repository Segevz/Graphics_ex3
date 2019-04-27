package edu.cg.scene;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import edu.cg.Logger;
import edu.cg.algebra.*;
import edu.cg.scene.camera.PinholeCamera;
import edu.cg.scene.lightSources.Light;
import edu.cg.scene.objects.Surface;

public class Scene {
	private String name = "scene";
	private int maxRecursionLevel = 1;
	private int antiAliasingFactor = 1; //gets the values of 1, 2 and 3
	private boolean renderRefractions = false;
	private boolean renderReflections = false;
	
	private PinholeCamera camera;
	private Vec ambient = new Vec(1, 1, 1); //white
	private Vec backgroundColor = new Vec(0, 0.5, 1); //blue sky
	private List<Light> lightSources = new LinkedList<>();
	private List<Surface> surfaces = new LinkedList<>();

	
	//MARK: initializers
	public Scene initCamera(Point eyePoistion, Vec towardsVec, Vec upVec,  double distanceToPlain) {
		this.camera = new PinholeCamera(eyePoistion, towardsVec, upVec,  distanceToPlain);
		return this;
	}
	
	public Scene initAmbient(Vec ambient) {
		this.ambient = ambient;
		return this;
	}
	
	public Scene initBackgroundColor(Vec backgroundColor) {
		this.backgroundColor = backgroundColor;
		return this;
	}
	
	public Scene addLightSource(Light lightSource) {
		lightSources.add(lightSource);
		return this;
	}
	
	public Scene addSurface(Surface surface) {
		surfaces.add(surface);
		return this;
	}
	
	public Scene initMaxRecursionLevel(int maxRecursionLevel) {
		this.maxRecursionLevel = maxRecursionLevel;
		return this;
	}
	
	public Scene initAntiAliasingFactor(int antiAliasingFactor) {
		this.antiAliasingFactor = antiAliasingFactor;
		return this;
	}
	
	public Scene initName(String name) {
		this.name = name;
		return this;
	}
	
	public Scene initRenderRefarctions(boolean renderRefarctions) {
		this.renderRefractions = renderRefarctions;
		return this;
	}
	
	public Scene initRenderReflections(boolean renderReflections) {
		this.renderReflections = renderReflections;
		return this;
	}
	
	//MARK: getters
	public String getName() {
		return name;
	}
	
	public int getFactor() {
		return antiAliasingFactor;
	}
	
	public int getMaxRecursionLevel() {
		return maxRecursionLevel;
	}
	
	public boolean getRenderRefractions() {
		return renderRefractions;
	}
	
	public boolean getRenderReflections() {
		return renderReflections;
	}
	
	@Override
	public String toString() {
		String endl = System.lineSeparator(); 
		return "Camera: " + camera + endl +
				"Ambient: " + ambient + endl +
				"Background Color: " + backgroundColor + endl +
				"Max recursion level: " + maxRecursionLevel + endl +
				"Anti aliasing factor: " + antiAliasingFactor + endl +
				"Light sources:" + endl + lightSources + endl +
				"Surfaces:" + endl + surfaces;
	}
	
	private transient ExecutorService executor = null;
	private transient Logger logger = null;
	
	private void initSomeFields(int imgWidth, int imgHeight, Logger logger) {
		this.logger = logger;
	}
	
	
	public BufferedImage render(int imgWidth, int imgHeight, double viewPlainWidth,Logger logger)
			throws InterruptedException, ExecutionException {
		// This method is invoked each time Render Scene button is invoked.
		// Use it to initialize additional fields you need.
		initSomeFields(imgWidth, imgHeight, logger);
		
		BufferedImage img = new BufferedImage(imgWidth, imgHeight, BufferedImage.TYPE_INT_RGB);
		camera.initResolution(imgHeight, imgWidth, viewPlainWidth);
		int nThreads = Runtime.getRuntime().availableProcessors();
		nThreads = nThreads < 2 ? 2 : nThreads;
		this.logger.log("Intitialize executor. Using " + nThreads + " threads to render " + name);
		executor = Executors.newFixedThreadPool(nThreads);
		
		@SuppressWarnings("unchecked")
		Future<Color>[][] futures = (Future<Color>[][])(new Future[imgHeight][imgWidth]);
		
		this.logger.log("Starting to shoot " +
			(imgHeight * imgWidth * antiAliasingFactor * antiAliasingFactor) +
			" rays over " + name);
		
		for(int y = 0; y < imgHeight; ++y)
			for(int x = 0; x < imgWidth; ++x)
				futures[y][x] = calcColor(x, y);
		
		this.logger.log("Done shooting rays.");
		this.logger.log("Waiting for results...");
		
		for(int y = 0; y < imgHeight; ++y)
			for(int x = 0; x < imgWidth; ++x) {
				Color color = futures[y][x].get();
				img.setRGB(x, y, color.getRGB());
			}
		
		executor.shutdown();
		
		this.logger.log("Ray tracing of " + name + " has been completed.");
		
		executor = null;
		this.logger = null;
		
		return img;
	}
	
	private Future<Color> calcColor(int x, int y) {
		return executor.submit(() -> {
			// TODO: You need to re-implement this method if you want to handle
			//       super-sampling. You're also free to change the given implementation as you like.
			Point centerPoint = camera.transform(x, y);
			Ray ray = new Ray(camera.getCameraPosition(), centerPoint);
			Vec color = calcColor(ray, 0);
			return color.toColor();
		});
	}
	
	private Vec calcColor(Ray ray, int recursionLevel) {
		// Halt condition
		if (recursionLevel >= this.maxRecursionLevel){
			return new Vec();
		}
		// Find minimum hit
		Hit minHit = this.findMinHit(ray);
		// If there are no intersections, return background color
		if (minHit == null)
			return this.backgroundColor;
		
		Point hitPoint = ray.getHittingPoint(minHit);
		Surface hitSurface = minHit.getSurface();
		// Ambient calculations
		Vec color = calcAmbientColor(hitSurface);

		// Iterate over light sources and calculate diffuse and specular coefficients
		for (Light light : this.lightSources) {
			Ray rayToLight = light.rayToLight(hitPoint);
			if (!this.isOccluded(light, rayToLight)) { 
				Vec intensity = light.intensity(hitPoint, rayToLight);
				color = color.add((calcDiffuseColor(minHit, rayToLight)).add(calcSpecularColor(minHit, rayToLight, ray.direction())).mult(intensity));

				// Reflective and refractive calculations
				if (this.renderReflections) {
					Vec reflectionColor = calcReflection(ray, recursionLevel + 1, minHit);
					color = color.add(reflectionColor);
				}
				if (this.renderRefractions) {
					if (minHit.isOnTransperentSurface()) {
						Vec refractionColor = calcRefraction(ray, recursionLevel + 1, minHit);
						color = color.add(refractionColor);
					}
				}
			}
		}
		return color;
	}

	private Vec calcRefraction(Ray ray, int recursionLevel, Hit hit) {
		Surface surface = hit.getSurface();
		Vec direction = Ops.refract(ray.direction(), hit.getNormalToSurface(), surface.n1(hit), surface.n2(hit));
		Vec intensity = new Vec(surface.refractionIntensity());
		return (this.calcColor(new Ray(ray.getHittingPoint(hit), direction), recursionLevel + 1)).mult(intensity);
	}

	private Vec calcReflection(Ray ray, int recursionLevel, Hit hit) {
		Vec direction = Ops.reflect(ray.direction(), hit.getNormalToSurface());
		Vec intensity = new Vec(hit.getSurface().reflectionIntensity());
		return (this.calcColor(new Ray(ray.getHittingPoint(hit), direction), recursionLevel)).mult(intensity);
	}

	/**
	 * Finds the closest intersection between ray and a surface
	 * Closest meaning distance between surface and image is smallest
	 * @param ray - the ray from a pixel
	 * @return minimum hit/intersection
	 */
	private Hit findMinHit(Ray ray) {
		// If there are no hits return null
		Hit minHit = null;
		for (Surface surface : this.surfaces){
			Hit currentHit = surface.intersect(ray);
			if (minHit == null || (currentHit != null && minHit.compareTo(currentHit) > 0)){
				minHit = currentHit;
			}
		}
		return minHit;
	}
	
	/**
	 * Checks if the given ray is occluded by any surface 
	 * before reaching the light source
	 * @param light -The light source
	 * @param ray - the ray to the light source
	 * @return true if the ray is occluded by any surface.
	 */
	private boolean isOccluded(Light light, Ray ray) {
		for (Surface surface : this.surfaces) {
			if (light.isOccludedBy(surface, ray))
				return true;
		}
		return false;
	}

	private Vec calcSpecularColor(Hit hit, Ray rayToLight, Vec V) {
		Vec N = hit.getNormalToSurface().normalize();
		Vec L = rayToLight.direction().neg().normalize();
		Vec Lreflected = Ops.reflect(L.neg(), N);
		double cosTheta = Lreflected.dot(V.neg());
		return cosTheta <= 0 ? new Vec() : hit.getSurface().Ks().mult(Math.pow(cosTheta, hit.getSurface().shininess()));
	}

	private Vec calcDiffuseColor(Hit hit, Ray ray){
		Vec N = hit.getNormalToSurface();
		Vec L = ray.direction();
		return hit.getSurface().Kd().mult(Math.max(N.dot(L),0));
	}

	private Vec calcAmbientColor(Surface surface) {
		return surface.Ka().mult(this.ambient);
	}
}
