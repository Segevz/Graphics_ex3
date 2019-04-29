package edu.cg.scene.objects;

import edu.cg.algebra.*;

public class Sphere extends Shape {
	private Point center;
	private double radius;

	public Sphere(Point center, double radius) {
		this.center = center;
		this.radius = radius;
	}

	public Sphere() {
		this(new Point(0, -0.5, -6), 0.5);
	}

	@Override
	public String toString() {
		String endl = System.lineSeparator();
		return "Sphere:" + endl + "Center: " + center + endl + "Radius: " + radius + endl;
	}

	public Sphere initCenter(Point center) {
		this.center = center;
		return this;
	}

	public Sphere initRadius(double radius) {
		this.radius = radius;
		return this;
	}

	@Override
	public Hit intersect(Ray ray) {
		double b = ray.direction().dot(ray.source().sub(this.center));
		double discriminant = Math.sqrt(Math.pow(b, 2) - (ray.source().distSqr(this.center) - (radius * radius)));
		double minT;
		Vec normal;
		if (Double.isNaN(discriminant) || (-b + discriminant) < Ops.epsilon) {
			return null;
		} else if (-b - discriminant > 0) {
			minT = -b - discriminant;
			normal = ray.add(minT).sub(this.center).normalize().neg();
		} else {
			minT = -b + discriminant;
			normal = ray.add(minT).sub(this.center).normalize();
		}
		return new Hit(minT, normal);
	}
}
