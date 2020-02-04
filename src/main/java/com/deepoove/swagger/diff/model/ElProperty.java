package com.deepoove.swagger.diff.model;

import io.swagger.models.properties.Property;

/**
 * property with expression Language grammar
 * 
 * @author Sayi
 * @version
 */
public class ElProperty {

	private String el;

	private Property leftProperty;
	private Property rightProperty;

	public Property getProperty() {
		return leftProperty;
	}

	public void setProperty(final Property property) {
		this.leftProperty = property;
	}

	public Property getRightProperty() {
		return rightProperty;
	}

	public void setRightProperty(final Property property) {
		this.rightProperty = property;
	}

	public String getEl() {
		return el;
	}

	public void setEl(final String el) {
		this.el = el;
	}

}
