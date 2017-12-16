package es.academy.solidgear.surveyx.services.requestparams;

import com.google.gson.annotations.SerializedName;

public class AnswerParams {
    @SerializedName("question_id")
    private int questionId;

    @SerializedName("answer_id")
    private int answerId;

    public AnswerParams(int questionId, int answerId) {
        this.questionId = questionId;
        this.answerId = answerId;
    }
}
