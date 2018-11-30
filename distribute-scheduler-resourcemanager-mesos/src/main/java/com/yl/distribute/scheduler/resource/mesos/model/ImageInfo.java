package com.yl.distribute.scheduler.resource.mesos.model;

import com.google.gson.Gson;

public class ImageInfo {

	/**
	 * Alt of the image tag. *
	 */
	private String alt;
	/**
	 * Src of the image tag. *
	 */
	private String src;

	public ImageInfo() {
	}

	public ImageInfo(String alt, String src) {
		this.alt = alt;
		this.src = src;
	}

	public String getAlt() {
		return alt;
	}

	public void setAlt(String alt) {
		this.alt = alt;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	/**
	 * Returns a JSON string corresponding to object state
	 *
	 * @return JSON representation
	 */
	public String toJSON() {
	    Gson gson = new Gson();
		return gson.toJson(this);	}

	@Override 
	public String toString() {
		return toJSON();
	}
}
