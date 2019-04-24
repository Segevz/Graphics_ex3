package edu.cg.scene.camera;

import edu.cg.UnimplementedMethodException;
import edu.cg.algebra.Point;
import edu.cg.algebra.Vec;

public class PinholeCamera {
	//TODO Add your fields
	private Point cameraPosition;
	private Vec towardsVec;
	private Vec upVec;
	private double distanceToPlain;
	private int Ry;
	private int Rx;
	private double viewPlainWidth;
	
	/**
	 * Initializes a pinhole camera model with default resolution 200X200 (RxXRy) and image width 2.
	 * @param cameraPosition - The position of the camera.
	 * @param towardsVec - The towards vector of the camera (not necessarily normalized). (-z coordinate)
	 * @param upVec - The up vector of the camera. (y coordinate)
	 * @param distanceToPlain - The distance of the camera (position) to the center point of the image-plain.
	 * 
	 */
	public PinholeCamera(Point cameraPosition, Vec towardsVec, Vec upVec, double distanceToPlain) {
	
		this.cameraPosition = cameraPosition; 
		this.towardsVec = towardsVec;
		this.upVec = upVec;
		this.distanceToPlain = distanceToPlain;
		// Initialize with default image width
		this.viewPlainWidth = 2;
		// Initialize with default resolution
		this.Ry = 200;
		this.Rx = 200;
	}
	/**
	 * Initializes the resolution and width of the image.
	 * @param height - the number of pixels in the y direction.
	 * @param width - the number of pixels in the x direction.
	 * @param viewPlainWidth - the width of the image plain in world coordinates.
	 */
	public void initResolution(int height, int width, double viewPlainWidth) {
		
		this.Ry = height;
		this.Rx = width;
		this.viewPlainWidth = viewPlainWidth;
		
	}

	/**
	 * Transforms from pixel coordinates to the center point of the corresponding pixel in model coordinates.
	 * @param x - the index of the x direction of the pixel.
	 * @param y - the index of the y direction of the pixel.
	 * @return the middle point of the pixel (x,y) in the model coordinates.
	 */
	public Point transform(int x, int y) {
		throw new UnimplementedMethodException("transform");
	}
	
	/**
	 * Returns a copy of the camera position
	 * @return a "new" point representing the camera position.
	 */
	public Point getCameraPosition() {
		return new Point (cameraPosition.x,cameraPosition.y,cameraPosition.z);
	}
}
