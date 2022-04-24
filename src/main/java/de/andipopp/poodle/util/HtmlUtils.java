package de.andipopp.poodle.util;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.router.RouterLink;

import de.andipopp.poodle.data.entity.User;
import de.andipopp.poodle.data.entity.polls.AbstractPoll;
import de.andipopp.poodle.views.usersettings.UserSettingsView;
import de.andipopp.poodle.views.vote.PollVoteView;

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
		RouterLink link = new RouterLink("", PollVoteView.class);
		return link.getHref()+"?pollId="+UUIDUtils.uuidToBase64url(poll.getId());
	}
	
	public static String linkToUser(User user) {
		RouterLink link = new RouterLink("", UserSettingsView.class);
		return link.getHref()+"?" + UserSettingsView.ID_PARAM_NAME +  "="+UUIDUtils.uuidToBase64url(user.getId());
	}
	
	
}
