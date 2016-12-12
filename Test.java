import java.io.IOException;
import java.util.List;

import org.eclipse.egit.github.core.Comment;
import org.eclipse.egit.github.core.CommitComment;
import org.eclipse.egit.github.core.PullRequest;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.IssueService;
import org.eclipse.egit.github.core.service.PullRequestService;
import org.eclipse.egit.github.core.service.RepositoryService;

public class Test {

	public static void main(String[] args) throws IOException {
		GitHubClient client = new GitHubClient();
		client.setCredentials("youruserhere", "yourpasswordhere");
		RepositoryService repoService = new RepositoryService(client);
		PullRequestService pullRequestService = new PullRequestService(client);
		Repository repo = repoService.getRepository("jenkinsci", "jenkins");
		
		IssueService is = new IssueService(client);
		int total = 0;
		for(int i = 1; i <= 2641; i++) {
			PullRequest pullRequest = null;
			try {
				pullRequest = pullRequestService.getPullRequest(repo, i);
			}
			catch(Exception e){
				System.out.println("Pull Request #" + i + " not found");
			}
			
			if(pullRequest != null) {
				String title = pullRequest.getTitle();
				String body = pullRequest.getBody();
				if(title != null) {
					title = pullRequest.getTitle().toLowerCase();
				}
				if(body != null) {
					body = pullRequest.getBody().toLowerCase();
				}
				boolean useful = false;
				if(title.contains("plugin") || title.contains("plug-in") || title.contains("plug in") ||
				  (body != null && (body.contains("plugin") || body.contains("plug-in") || body.contains("plug in")))) {
					useful = true;
				}
				if(!useful) {
					List<Comment> list = is.getComments(repo, i);
					for(Comment c : list) {
						String commentBody = c.getBody().toLowerCase();
						if(commentBody.contains("plugin") || commentBody.contains("plug-in") || commentBody.contains("plug in")){
							useful = true;
						}		
					}
				}
				if(!useful) {
					List<CommitComment> list = pullRequestService.getComments(repo, i);
					for(CommitComment c : list) {
						String commentBody = c.getBody().toLowerCase();
						if(commentBody.contains("plugin") || commentBody.contains("plug-in") || commentBody.contains("plug in")){
							useful = true;
						}
					}
				}
				if(useful) {
					total++;
					System.out.println(pullRequest.getNumber() + " " + pullRequest.getState());
				}
			}
		}
		System.out.println("Total: " + total);
	}
}