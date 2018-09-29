package jp.silverbullet.trash.speceditor2;

import java.util.List;

import jp.silverbullet.SvProperty;

public interface CandidatesFatory {

	List<String> getAnswerCandidates();

	List<String> getElementCandidates();

	List<String> getOperatorCandidates();

	void setElement(String element);
}
