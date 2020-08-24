package com.holalola.webhook.facebook.response.message;

import java.util.List;

import com.holalola.webhook.facebook.templates.TextQuickReply;

public class MessageIn {

	private String mid;
	private long seq;
	private String text;
	private boolean is_echo;
	private long app_id;
	private Long sticker_id;
	// TODO: Como saber si es un location?
	private TextQuickReply quick_reply; // TODO: Ver si la estructura esta
											// bien para texto
	private List<Attachment> attachments; // TODO: Ver Documentacion para ver si
											// se usa otro attachment
	// https://developers.facebook.com/docs/messenger-platform/webhook-reference/message#multimedia_payload

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public long getSeq() {
		return seq;
	}

	public void setSeq(long seq) {
		this.seq = seq;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public List<Attachment> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<Attachment> attachments) {
		this.attachments = attachments;
	}

	public TextQuickReply getQuick_reply() {
		return quick_reply;
	}

	public void setQuick_reply(TextQuickReply quick_reply) {
		this.quick_reply = quick_reply;
	}

	public boolean isIs_echo() {
		return is_echo;
	}

	public void setIs_echo(boolean is_echo) {
		this.is_echo = is_echo;
	}

	public long getApp_id() {
		return app_id;
	}

	public void setApp_id(long app_id) {
		this.app_id = app_id;
	}

	public Long getSticker_id() {
		return sticker_id;
	}

	public void setSticker_id(Long sticker_id) {
		this.sticker_id = sticker_id;
	}

}
