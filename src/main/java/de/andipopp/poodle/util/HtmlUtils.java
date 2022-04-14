package de.andipopp.poodle.util;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.router.RouterLink;

import de.andipopp.poodle.data.entity.polls.AbstractPoll;
import de.andipopp.poodle.views.poll.PollView;

public class HtmlUtils {

	public static Html pollShareScript(AbstractPoll<?, ?> poll) {
		
		String script = "function share_" + UUIDUtils.uuidToBase64url(poll.getId()) +  "() {\n" +
			"if (navigator.share) {\n" +
			   "navigator.share({\n" +
			      "title: '" + poll.getTitle() + "',\n" +
			      "url: window.location.href\n" + 
			    "}).then(() => {\n" +
			      //keep this spot, just in case
			    "})\n" +
			    ".catch(console.error);\n" +
			    "} else {\n" +
			        "navigator.clipboard.writeText(window.location.href);\n" +
			        "alert('URL copied to clipboard');\n" +
			    "}\n}";
		return new Html("<script type=\"text/javascript\">\n" + script + "\n</script>");
		
	}
	
	public static String linkToPoll(AbstractPoll<?, ?> poll) {
		RouterLink link = new RouterLink("", PollView.class);
		return link.getHref()+"?pollId="+UUIDUtils.uuidToBase64url(poll.getId());
	}
	
	
}
