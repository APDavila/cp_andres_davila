package com.holalola.webhook.facebook.templates;

import java.util.List;

public class TextMessage extends FacebookRequestGeneral {

	private String text;
	private List<QuickReplyGeneral> quick_replies; // max 11

	public TextMessage(String text) {
		this.text = text;
	}

	public TextMessage(String text, List<QuickReplyGeneral> quick_replies) {
		this.text = text;
		this.quick_replies = quick_replies;
	}

	public String getText() {
		return text;
	}

	public List<QuickReplyGeneral> getQuick_replies() {
		return quick_replies;
	}

	public void setQuick_replies(List<QuickReplyGeneral> quick_replies) {
		this.quick_replies = quick_replies;
	}

}
