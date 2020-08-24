package com.holalola.webhook.facebook.templates;

import java.util.List;

public class TextMessageV2 extends Facebook{
	

		private String text;
		private List<QuickReplyGeneral> quick_replies; // max 11

		public TextMessageV2(String text) {
			this.text = text;
		}

		public TextMessageV2(String text, List<QuickReplyGeneral> quick_replies) {
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
