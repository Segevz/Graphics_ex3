package edu.cg.scene.lightSources;

import edu.cg.algebra.Point;
import edu.cg.algebra.Ray;
import edu.cg.algebra.Vec;
import edu.cg.scene.objects.Surface;

public class Spotlight extends PointLight {
	private Vec direction;
	
	public Spotlight initDirection(Vec direction) {
		this.direction = direction;
		return this;
	}
	
	@Override
	public String toString() {
		String endl = System.lineSeparator();
		return "Spotlight: " + endl +
				description() + 
				"Direction: " + direction + endl;
	}
	
	@Override
	public Spotlight initPosition(Point position) {
		return (Spotlight)super.initPosition(position);
	}
	
	@Override
	public Spotlight initIntensity(Vec intensity) {
		return (Spotlight)super.initIntensity(intensity);
	}
	
	@Override
	public Spotlight initDecayFactors(double q, double l, double c) {
		return (Spotlight)super.initDecayFactors(q, l, c);
	}
	
	/**
	 * Checks if the given surface occludes the light-source. The surface occludes the light source
	 * if the given ray first intersects the surface before reaching the light source.
	 * @param surface -The given surface
	 * @param rayToLight - the ray to the light source
	 * @return true if the ray is occluded by the surface.
	 */
    @Override
    public boolean isOccludedBy(Surface surface, Ray rayToLight) {
        if (rayToLight.direction().neg().dot(this.direction.normalize()) < 1.0E-5) {
            return true;
        }
        return super.isOccludedBy(surface, rayToLight);
    }
   
    /**
	 * Returns the light intensity at the specified point.
	 * @param hittingPoint - The given point
	 * @param rayToLight - A ray to the light source (this is relevant for point-light and spotlight)
	 * @return A vector representing the light intensity (the r,g and b channels). 
	 */
    @Override
    public Vec intensity(Point hittingPoint, Ray rayToLight) {
        Vec D = this.direction.normalize().neg();
        return super.intensity(hittingPoint, rayToLight).mult(D.dot(rayToLight.direction()));
    }
}
