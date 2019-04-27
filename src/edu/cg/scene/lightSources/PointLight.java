package edu.cg.scene.lightSources;

import edu.cg.algebra.Hit;
import edu.cg.algebra.Point;
import edu.cg.algebra.Ray;
import edu.cg.algebra.Vec;
import edu.cg.scene.objects.Surface;

public class PointLight extends Light {
	protected Point position;
	
	//Decay factors:
	protected double kq = 0.01;
	protected double kl = 0.1;
	protected double kc = 1;
	
	protected String description() {
		String endl = System.lineSeparator();
		return "Intensity: " + intensity + endl +
				"Position: " + position + endl +
				"Decay factors: kq = " + kq + ", kl = " + kl + ", kc = " + kc + endl;
	}
	
	@Override
	public String toString() {
		String endl = System.lineSeparator();
		return "Point Light:" + endl + description();
	}
	
	@Override
	public PointLight initIntensity(Vec intensity) {
		return (PointLight)super.initIntensity(intensity);
	}

	@Override
	public Ray rayToLight(Point fromPoint) {
		return new Ray(fromPoint, this.position);
	}

	/**
	 * Checks if the given surface occludes the light-source. The surface occludes the light source
	 * if the given ray first intersects the surface before reaching the light source.
	 * @param surface -The given surface
	 * @param rayToLight - the ray to the light source
	 * @return true if the ray is occluded by the surface and the surface is between
	 * object and light source
	 */
	@Override
	public boolean isOccludedBy(Surface surface, Ray rayToLight) {
		// If surface does not intersect with ray return false
		Hit hit = surface.intersect(rayToLight);
	    if (hit == null) {
	    	return false;
	    }
	    // Otherwise, the surface intersects with the ray, if the intersection is closer to
	    // object than the light source then return true, otherwise false
	    Point sourceOfRay = rayToLight.source();
	    	return sourceOfRay.distSqr(this.position) > sourceOfRay.distSqr(rayToLight.getHittingPoint(hit));
	}

	/**
	 * Returns the light intensity at the specified point.
	 * @param hittingPoint - The given point
	 * @param rayToLight - A ray to the light source (this is relevant for point-light and spotlight)
	 * @return A vector representing the light intensity which decreases in intensity (the r,g and b channels). 
	 */
    @Override
    public Vec intensity(Point hittingPoint, Ray rayToLight) {
        double distance = hittingPoint.dist(this.position);
        // Calculates and returns decrease in intensity 
        return this.intensity.mult(1.0 / (this.kc + (this.kl * distance) + (this.kq * distance * distance)));
    }

	public PointLight initPosition(Point position) {
		this.position = position;
		return this;
	}
	
	public PointLight initDecayFactors(double kq, double kl, double kc) {
		this.kq = kq;
		this.kl = kl;
		this.kc = kc;
		return this;
	}
}
