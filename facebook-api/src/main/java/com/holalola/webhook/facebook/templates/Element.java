package com.holalola.webhook.facebook.templates;

import java.util.List;

public class Element {

	private String title;
	private String image_url;
	private String subtitle;
	private DefaultAction default_action;
	private List<ButtonGeneral> buttons; // max 3

	public Element(String title, String image_url, String subtitle, DefaultAction default_action,
			List<ButtonGeneral> buttons) {
		this.title = title;
		this.image_url = image_url;
		this.subtitle = subtitle;
		this.default_action = default_action;
		this.buttons = buttons;
	}
	
	public Element(String title, List<ButtonGeneral> buttons) {
		this.title = title;
		this.buttons = buttons;
	}

	public Element(String title, String image_url, List<ButtonGeneral> buttons) {
		this.title = title;
		this.image_url = image_url;
		this.buttons = buttons;
	}

	public Element(String title, String image_url, String subtitle, List<ButtonGeneral> buttons) {
		this.title = title;
		this.image_url = image_url;
		this.subtitle = subtitle;
		this.buttons = buttons;
	}

	public Element(String title, String subtitle) {
		this.title = title;
		this.subtitle = subtitle;
	}
	
	

	@Override
	public String toString() {
		return "Element [title=" + title + ", image_url=" + image_url + ", subtitle=" + subtitle + ", default_action="
				+ default_action + ", buttons=" + buttons + "]";
	}

	public String getTitle() {
		return title;
	}

	public String getImage_url() {
		return image_url;
	}

	public String getSubtitle() {
		return subtitle;
	}

	public DefaultAction getDefault_action() {
		return default_action;
	}

	public List<ButtonGeneral> getButtons() {
		return buttons;
	}

}
