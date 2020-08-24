package com.holalola.webhook.facebook.payload;

import java.util.List;

import com.holalola.webhook.facebook.templates.Element;

public class GenericTemplatePayload extends PayloadGeneral {
	
	public static final String SQUARE_ASPECT_RATIO = "square";
	public static final String HORIZONTAL_ASPECT_RATIO = "horizontal";

	private final String template_type = "generic";
	private String image_aspect_ratio;
	private List<Element> elements;

	public GenericTemplatePayload(List<Element> elements) {
		this.elements = elements;
	}
	
	public GenericTemplatePayload(List<Element> elements, String imageAspectRatio) {
		this.elements = elements;
		this.image_aspect_ratio = imageAspectRatio;
	}

	public List<Element> getElements() {
		return elements;
	}

	public void setElements(List<Element> elements) {
		this.elements = elements;
	}

	public String getTemplate_type() {
		return template_type;
	}

	public String getImage_aspect_ratio() {
		return image_aspect_ratio;
	}

	public void setImage_aspect_ratio(String image_aspect_ratio) {
		this.image_aspect_ratio = image_aspect_ratio;
	}

}
