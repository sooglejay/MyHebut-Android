package com.myhebut.exam;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.myhebut.activity.R;
import com.myhebut.classes.VerticalImageSpan;
import com.myhebut.entity.Question;

public class ExamResultPageFragment extends Fragment {


    private Question question;

    private String answer;

    @ViewInject(R.id.tv_exam_content_text)
    private TextView content;
    @ViewInject(R.id.cbx_answer_a)
    private CheckBox answerA;
    @ViewInject(R.id.cbx_answer_b)
    private CheckBox answerB;
    @ViewInject(R.id.cbx_answer_c)
    private CheckBox answerC;
    @ViewInject(R.id.cbx_answer_d)
    private CheckBox answerD;
    @ViewInject(R.id.cbx_answer_e)
    private CheckBox answerE;

    public ExamResultPageFragment(Question question, String answer) {
        this.question = question;
        this.answer = answer;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exam_practice_content, null);
        ViewUtils.inject(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setCbxsEnable(false);
        initData();
        initAnswer();
    }

    private void initData() {

        Drawable drawable;
        if (question.getType() == 0) {
            drawable = getResources().getDrawable(R.mipmap.img_exam_danxuan);
        } else {
            drawable = getResources().getDrawable(R.mipmap.img_exam_duoxuan);
            // 若E选项不为空,则说明含有answerE
            if (!TextUtils.isEmpty(question.getAnswerE())) {
                answerE.setVisibility(View.VISIBLE);
                answerE.setText(question.getAnswerE());
            }
        }
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        VerticalImageSpan imageSpan = new VerticalImageSpan(drawable);
        // 添加显示题目类型的图片并设置题目内容
        SpannableString spanText = new SpannableString("   " + question.getContent());
        spanText.setSpan(imageSpan, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
        content.setText(spanText);

        answerA.setText(question.getAnswerA());
        answerB.setText(question.getAnswerB());
        answerC.setText(question.getAnswerC());
        answerD.setText(question.getAnswerD());
    }

    private void initAnswer() {
        if (question.getType() == 0) {
            showTrueAnswer(question.getAnswerT());
            if (answer != null) {
                showFalseAnswer(answer);
            }
        } else {
            char[] arrayAnswerT = question.getAnswerT().toCharArray();
            // 正确答案显示字母
            for (int i = 0; i < arrayAnswerT.length; i++) {
                showTrueAnswerByLetter(String.valueOf(arrayAnswerT[i]));
            }
            if (answer != null) {
                char[] arrayAnswer = answer.toCharArray();
                // 如果已选择且正确,则显示对勾;否则显示叉叉
                for (int i = 0; i < arrayAnswer.length; i++) {
                    if (question.getAnswerT().indexOf(arrayAnswer[i]) != -1) {
                        showTrueAnswer(String.valueOf(arrayAnswer[i]));
                    } else {
                        showFalseAnswer(String.valueOf(arrayAnswer[i]));
                    }
                }
            }
        }
    }


    // 显示正确答案打上对勾
    private void showTrueAnswer(String answerT) {
        Drawable drawable = getResources().getDrawable(R.mipmap.ic_exam_answer_true);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());

        changeTrueAnswerImg(answerT, drawable);
    }

    // 显示正确答案打上叉叉
    private void showFalseAnswer(String answerT) {
        Drawable drawable = getResources().getDrawable(R.mipmap.ic_exam_answer_false);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());

        changeTrueAnswerImg(answerT, drawable);
    }

    // 显示正确答案标记字母
    private void showTrueAnswerByLetter(String answerT) {
        Drawable drawable = null;
        switch (answerT) {
            case "A":
                drawable = getResources().getDrawable(R.mipmap.ic_exam_a_true);
                break;
            case "B":
                drawable = getResources().getDrawable(R.mipmap.ic_exam_b_true);
                break;
            case "C":
                drawable = getResources().getDrawable(R.mipmap.ic_exam_c_true);
                break;
            case "D":
                drawable = getResources().getDrawable(R.mipmap.ic_exam_d_true);
                break;
            case "E":
                drawable = getResources().getDrawable(R.mipmap.ic_exam_e_true);
                break;
            default:
                break;
        }
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());

        changeTrueAnswerImg(answerT, drawable);
    }

    // 改变选项前的图片
    private void changeTrueAnswerImg(String answerT, Drawable drawable) {
        switch (answerT) {
            case "A":
                answerA.setCompoundDrawables(drawable, null, null, null);
                break;
            case "B":
                answerB.setCompoundDrawables(drawable, null, null, null);
                break;
            case "C":
                answerC.setCompoundDrawables(drawable, null, null, null);
                break;
            case "D":
                answerD.setCompoundDrawables(drawable, null, null, null);
                break;
            case "E":
                answerE.setCompoundDrawables(drawable, null, null, null);
                break;
            default:
                break;
        }
    }


    private void setCbxsEnable(boolean status) {
        answerA.setEnabled(status);
        answerB.setEnabled(status);
        answerC.setEnabled(status);
        answerD.setEnabled(status);
        answerE.setEnabled(status);
    }


    public String getQuestionId() {
        return question.getQuestionId() + "";
    }


}
