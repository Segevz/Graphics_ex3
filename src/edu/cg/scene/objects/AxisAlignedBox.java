package edu.cg.scene.objects;

import edu.cg.UnimplementedMethodException;
import edu.cg.algebra.Hit;
import edu.cg.algebra.Point;
import edu.cg.algebra.Ray;
import edu.cg.algebra.Vec;

public class AxisAlignedBox extends Shape {
	private Point minPoint;
	private Point maxPoint;
	private String name = "";
	static private int CURR_IDX;

	/**
	 * Creates an axis aligned box with a specified minPoint and maxPoint.
	 */
	public AxisAlignedBox(Point minPoint, Point maxPoint) {
		this.minPoint = minPoint;
		this.maxPoint = maxPoint;
		name = new String("Box " + CURR_IDX);
		CURR_IDX += 1;
		fixBoundryPoints();
	}

	/**
	 * Creates a default axis aligned box with a specified minPoint and maxPoint.
	 */
	public AxisAlignedBox() {
		minPoint = new Point(-1.0, -1.0, -1.0);
		maxPoint = new Point(1.0, 1.0, 1.0);
	}
	
	/**
	 * This methods fixes the boundary points minPoint and maxPoint so that the values are consistent.
	 */
	private void fixBoundryPoints() {
		double min_x = Math.min(minPoint.x, maxPoint.x), max_x = Math.max(minPoint.x, maxPoint.x),
				min_y = Math.min(minPoint.y, maxPoint.y), max_y = Math.max(minPoint.y, maxPoint.y),
				min_z = Math.min(minPoint.z, maxPoint.z), max_z = Math.max(minPoint.z, maxPoint.z);
		minPoint = new Point(min_x, min_y, min_z);
		maxPoint = new Point(max_x, max_y, max_z);
	}
	
	@Override
	public String toString() {
		String endl = System.lineSeparator();
		return name + endl + "Min Point: " + minPoint + endl + "Max Point: " + maxPoint + endl;
	}
	
	//Initializers
	public AxisAlignedBox initMinPoint(Point minPoint) {
		this.minPoint = minPoint;
		fixBoundryPoints();
		return this;
	}

	public AxisAlignedBox initMaxPoint(Point maxPoint) {
		this.maxPoint = maxPoint;
		fixBoundryPoints();
		return this;
	}
	public boolean isInBox(Point p){
		return isSmallerThen(minPoint, p) && isSmallerThen(p, maxPoint);
	}
	private boolean isSmallerThen(Point p1, Point p2){
		return p1.x < p2.x && p1.y < p2.y && p1.z < p2.z;
	}

	@Override
	public Hit intersect(Ray ray) {
		// TODO You need to implement this method. 
		// See documentation in Intersectable.java base class. 
//		throw new UnimplementedMethodException("intersect");
		Vec xzNormal = new Vec(0, 1, 0);
		Vec xyNormal = new Vec(0, 0, -1);
		Vec yzNormal = new Vec(1, 0, 0);

		Plain xzMin = new Plain(xzNormal, minPoint);
		Plain xyMin = new Plain(xyNormal, minPoint);
		Plain yzMin = new Plain(yzNormal, minPoint);
		Plain xyMax = new Plain(xyNormal, maxPoint);
		Plain xzMax = new Plain(xzNormal, maxPoint);
		Plain yzMax = new Plain(yzNormal, maxPoint);
		Hit minHit = null;
		Hit currentHit;

		if ((currentHit = xyMin.intersect(ray)) != null && isInBox(ray.getHittingPoint(currentHit))){
			if (minHit != null){
				if (ray.source().dist(ray.getHittingPoint(minHit)) > ray.source().dist(ray.getHittingPoint(currentHit))){
					minHit = currentHit;
				}
			}else {
				minHit = currentHit;
			}
		}
		if ((currentHit = xzMin.intersect(ray)) != null && isInBox(ray.getHittingPoint(currentHit))){
			if (minHit != null){
				if (ray.source().dist(ray.getHittingPoint(minHit)) > ray.source().dist(ray.getHittingPoint(currentHit))){
					minHit = currentHit;
				}
			}else {
				minHit = currentHit;
			}
		}
		if ((currentHit = yzMin.intersect(ray)) != null && isInBox(ray.getHittingPoint(currentHit))){
			if (minHit != null){
				if (ray.source().dist(ray.getHittingPoint(minHit)) > ray.source().dist(ray.getHittingPoint(currentHit))){
					minHit = currentHit;
				}
			}else {
				minHit = currentHit;
			}
		}
		if ((currentHit = xyMax.intersect(ray)) != null && isInBox(ray.getHittingPoint(currentHit))){
			if (minHit != null){
				if (ray.source().dist(ray.getHittingPoint(minHit)) > ray.source().dist(ray.getHittingPoint(currentHit))){
					minHit = currentHit;
				}
			}else {
				minHit = currentHit;
			}
		}
		if ((currentHit = xzMax.intersect(ray)) != null && isInBox(ray.getHittingPoint(currentHit))){
			if (minHit != null){
				if (ray.source().dist(ray.getHittingPoint(minHit)) > ray.source().dist(ray.getHittingPoint(currentHit))){
					minHit = currentHit;
				}
			}else {
				minHit = currentHit;
			}
		}
		if ((currentHit = yzMax.intersect(ray)) != null && isInBox(ray.getHittingPoint(currentHit))){
			if (minHit != null){
				if (ray.source().dist(ray.getHittingPoint(minHit)) > ray.source().dist(ray.getHittingPoint(currentHit))){
					minHit = currentHit;
				}
			}else {
				minHit = currentHit;
			}
		}
		if ((currentHit = xzMin.intersect(ray)) != null && isInBox(ray.getHittingPoint(currentHit))){
			if (minHit != null){
				if (ray.source().dist(ray.getHittingPoint(minHit)) > ray.source().dist(ray.getHittingPoint(currentHit))){
					minHit = currentHit;
				}
			}else {
				minHit = currentHit;
			}
		}
		return minHit;
	}
}
