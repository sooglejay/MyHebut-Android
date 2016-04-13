package com.myhebut.exam;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.RequestParams;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lidroid.xutils.http.client.HttpRequest.HttpMethod;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.lidroid.xutils.view.annotation.event.OnClick;
import com.myhebut.activity.R;
import com.myhebut.classes.VerticalImageSpan;
import com.myhebut.entity.JsonCommon;
import com.myhebut.entity.Question;
import com.myhebut.manager.ExamManager;
import com.myhebut.utils.HttpUtil;
import com.myhebut.utils.UrlUtil;

import java.util.ArrayList;
import java.util.List;

public class ExamPageFragment extends Fragment {
    // true为考试练习,则不需要自动判卷
    private boolean isExam;

    private String examAnswer;

    private boolean isFinished;

    private int userId;

    private Question question;

    private List<String> answers = new ArrayList<>();

    private String answerT;

    // 题目类型
    private int type;

    private ExamManager manager;

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

    @ViewInject(R.id.btn_answer_confirm)
    private Button mBtnConfirm;

    private View view;

    private Gson gson;

    @OnClick(R.id.btn_answer_confirm)
    private void answerConfirm(View v) {
        // 选择按钮禁用
        setCbxsEnable(false);
        // 提交按钮隐藏
        mBtnConfirm.setVisibility(View.GONE);
        if (answerA.isChecked()) {
            answers.add("A");
        }
        if (answerB.isChecked()) {
            answers.add("B");
        }
        if (answerC.isChecked()) {
            answers.add("C");
        }
        if (answerD.isChecked()) {
            answers.add("D");
        }
        if (answerE.isChecked()) {
            answers.add("E");
        }
        // 正确答案显示字母
        Log.d("exam", "answerT:" + answerT);
        char[] arrayAnswerT = answerT.toCharArray();
        for (int i = 0; i < arrayAnswerT.length; i++) {
            showTrueAnswerByLetter(String.valueOf(arrayAnswerT[i]));
            Log.d("exam", "answerT Letter:" + String.valueOf(arrayAnswerT[i]));
        }
        // 如果有一个选项错误,则为false
        boolean flag = true;
        // 如果已选择且正确,则显示对勾;否则显示叉叉
        for (int i = 0; i < answers.size(); i++) {
            if (answerT.indexOf(answers.get(i)) != -1) {
                showTrueAnswer(answers.get(i));
            } else {
                showFalseAnswer(answers.get(i));
                flag = false;
            }
        }
        // 如果flag为true,且选项数量与正确答案相同,说明没有错选或漏选,即正确
        if (flag && answers.size() == answerT.length()) {
            if (!isFinished) {
                // 正确数目加1并显示
                manager.sendAddTrueCount();
                // 答题卡题号变色
                manager.sendChangeCard2True();
            }
            // 延迟跳转
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    manager.sendNextPage();
                }
            }, 200);
        } else {
            if (!isFinished) {
                // 错误数目加1并显示
                manager.sendAddFalseCount();
                // 答题卡题号变色
                manager.sendChangeCard2False();
                // 添加到错题集
                addNote();
            }

        }
        // 设置题目已答
        isFinished = true;
    }

    public ExamPageFragment(Question question, int userId, boolean isExam) {
        this.question = question;
        this.userId = userId;
        this.isExam = isExam;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_exam_practice_content, null);
        ViewUtils.inject(this, view);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        initEvent();

        // 对于fragment被销毁后重新create没有保存提交信息,这里进行手动操作
        if (isFinished && type == 1 && !isExam) {
            answerConfirm(view);
        }

        // 根据模式进行切换
        if (ExamActivity.mode) {
            changeToLearnMode();
        } else {
            changeToAnswerMode();
        }
    }

    /*
     * 暂时不添加viewpager动态删除页面功能 TODO
     */
    private void initAnswer() {
        // 设置按钮未选
        answerA.setChecked(false);
        answerB.setChecked(false);
        answerC.setChecked(false);
        answerD.setChecked(false);
        // 恢复按钮可选
        setCbxsEnable(true);
        // 恢复按钮前的图片
        showDefaultAnswerImg();
        // 重新进行选择
        for (int i = 0; i < answers.size(); i++) {
            switch (answers.get(i)) {
                case "A":
                    answerA.setChecked(true);
                    break;
                case "B":
                    answerB.setChecked(true);
                    break;
                case "C":
                    answerC.setChecked(true);
                    break;
                case "D":
                    answerD.setChecked(true);
                    break;
                default:
                    break;
            }
        }
    }

    public void changeToAnswerMode() {
        if (!isFinished && !isExam) {
            // 取消选择按钮禁用
            setCbxsEnable(true);
            if (type == 0) {
                hideTrueAnswer(answerT);
            } else {
                // 未答的多选题按钮显示
                mBtnConfirm.setVisibility(View.VISIBLE);

                char[] arrayAnswerT = answerT.toCharArray();
                for (int i = 0; i < arrayAnswerT.length; i++) {
                    hideTrueAnswer(String.valueOf(arrayAnswerT[i]));
                }
            }
        }
    }

    public void changeToLearnMode() {
        // 当前为学习模式且题目未答
        if (!isFinished) {
            // 所有选择按钮禁用
            setCbxsEnable(false);
            if (type == 0) {
                // 无论对错,正确选项打钩
                showTrueAnswer(answerT);
            } else {
                // 未回答的题要隐藏提交按钮
                mBtnConfirm.setVisibility(View.GONE);

                char[] arrayAnswerT = answerT.toCharArray();
                for (int i = 0; i < arrayAnswerT.length; i++) {
                    showTrueAnswer(String.valueOf(arrayAnswerT[i]));
                }

            }
        }
    }

    private void initEvent() {
        if (isExam) {
            answerA.setOnCheckedChangeListener(new ExamCbxListener("A"));
            answerB.setOnCheckedChangeListener(new ExamCbxListener("B"));
            answerC.setOnCheckedChangeListener(new ExamCbxListener("C"));
            answerD.setOnCheckedChangeListener(new ExamCbxListener("D"));
            answerE.setOnCheckedChangeListener(new ExamCbxListener("D"));
        } else {
            // 普通模式单选题要对选择按钮添加事件监听
            if (type == 0) {
                answerA.setOnCheckedChangeListener(new CommonCbxListener("A"));
                answerB.setOnCheckedChangeListener(new CommonCbxListener("B"));
                answerC.setOnCheckedChangeListener(new CommonCbxListener("C"));
                answerD.setOnCheckedChangeListener(new CommonCbxListener("D"));
                answerE.setOnCheckedChangeListener(new CommonCbxListener("D"));
            }
        }

    }

    private void initData() {
        gson = new Gson();

        manager = ExamManager.getInstance();
        type = question.getType();
        answerT = question.getAnswerT();

        Drawable drawable;
        if (type == 0) {
            drawable = getResources().getDrawable(R.mipmap.img_exam_danxuan);
        } else {
            drawable = getResources().getDrawable(R.mipmap.img_exam_duoxuan);
            // 多选题在不是考试模式的情况下需要显示提交按钮
            if (!isExam) {
                mBtnConfirm.setVisibility(View.VISIBLE);
            }
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

    class CommonCbxListener implements CompoundButton.OnCheckedChangeListener {

        private String answer;

        public CommonCbxListener(String answer) {
            super();
            this.answer = answer;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            // 将用户选项记录下来
            answers.add(answer);
            // 所有选择按钮禁用
            setCbxsEnable(false);
            // 无论对错,正确选项打钩
            showTrueAnswer(answerT);
            // 如果选错,错误选项打叉
            if (!answer.equals(answerT)) {
                Drawable drawable = getResources().getDrawable(R.mipmap.ic_exam_answer_false);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                buttonView.setCompoundDrawables(drawable, null, null, null);

                // (针对销毁的fragment仍保留状态信息,答题卡和题数等信息保存在Activity中)
                if (!isFinished) {
                    // 错误数目加1并显示
                    manager.sendAddFalseCount();
                    // 答题卡题号变色
                    manager.sendChangeCard2False();
                    // 添加到错题集
                    addNote();
                }

            } else { // 回答正确自动跳转下一页

                if (!isFinished) {
                    // 正确数目加1并显示
                    manager.sendAddTrueCount();
                    // 答题卡题号变色
                    manager.sendChangeCard2True();
                    // 延迟跳转
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            manager.sendNextPage();
                        }
                    }, 200);
                }

            }
            isFinished = true;
        }
    }

    class ExamCbxListener implements CompoundButton.OnCheckedChangeListener {

        private String answer;

        public ExamCbxListener(String answer) {
            super();
            this.answer = answer;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            Log.d("exam", "answerT:" + question.getAnswerT());
            // 将checkbox设置为单选模式,多选题无需设置
            if (isChecked && type == 0) {
                answerA.setChecked(false);
                answerB.setChecked(false);
                answerC.setChecked(false);
                answerD.setChecked(false);
                answerE.setChecked(false);

                setCbxChecked(answer);

                if (question.getAnswerT().equals(answer)) {
                    manager.sendChangeAnswerStatus2T();
                } else {
                    manager.sendChangeAnswerStatus2F();
                    // 仅需记录错题的答案
                    examAnswer = answer;
                }
            }
            if (type == 1) {
                String answer = getMultipleAnswers();
                if (question.getAnswerT().equals(answer)) {
                    manager.sendChangeAnswerStatus2T();
                } else {
                    manager.sendChangeAnswerStatus2F();
                    // 仅需记录错题的答案
                    examAnswer = answer;
                }
            }

            // 只要有一个答案选中,答题卡对应题号就变色
            if (isChecked || answerA.isChecked() || answerB.isChecked() || answerC.isChecked() || answerD.isChecked()) {
                manager.sendChangeCard2Checked();
            } else {
                manager.sendChangeCard2Default();
            }
        }
    }

    private String getMultipleAnswers() {
        String answer = "";
        if (answerA.isChecked())
            answer += "A";
        if (answerB.isChecked())
            answer += "B";
        if (answerC.isChecked())
            answer += "C";
        if (answerD.isChecked())
            answer += "D";
        if (answerE.isChecked())
            answer += "E";
        return answer;
    }

    // 添加到错题集
    private void addNote() {
        HttpUtils http = HttpUtil.getHttp();
        RequestParams params = new RequestParams();
        params.addBodyParameter("userId", userId + "");
        params.addBodyParameter("questionId", question.getQuestionId() + "");
        http.send(HttpMethod.POST, UrlUtil.addNoteUrl(), params, new RequestCallBack<String>() {

            @Override
            public void onSuccess(ResponseInfo<String> responseInfo) {
                try{
                    String jsonData = responseInfo.result;
                    JsonCommon jsonCommon = gson.fromJson(jsonData, JsonCommon.class);
                } catch(Exception e){
                    Toast.makeText(getContext(), "连接失败,请检查网络设置", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(HttpException error, String msg) {
                Toast.makeText(getContext(), "连接失败,请检查网络设置", Toast.LENGTH_SHORT).show();
            }
        });

    }

    // 显示正确答案打上对勾
    private void showTrueAnswer(String answerT) {
        Drawable drawable = getResources().getDrawable(R.mipmap.ic_exam_answer_true);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());

        changeTrueAnswerImg(answerT, drawable);
    }

    // 恢复答题模式,隐藏正确答案
    private void hideTrueAnswer(String answerT) {
        Drawable drawable = null;
        switch (answerT) {
            case "A":
                drawable = getResources().getDrawable(R.drawable.exam_answer_a);
                break;
            case "B":
                drawable = getResources().getDrawable(R.drawable.exam_answer_b);
                break;
            case "C":
                drawable = getResources().getDrawable(R.drawable.exam_answer_c);
                break;
            case "D":
                drawable = getResources().getDrawable(R.drawable.exam_answer_d);
                break;
            case "E":
                drawable = getResources().getDrawable(R.drawable.exam_answer_e);
                break;
            default:
                break;
        }
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
                Log.d("exam","??????????????????");
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

    // 恢复选项的默认图片
    private void showDefaultAnswerImg() {
        Drawable drawable = getResources().getDrawable(R.mipmap.ic_exam_a_normal);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        answerA.setCompoundDrawables(drawable, null, null, null);

        drawable = getResources().getDrawable(R.mipmap.ic_exam_b_normal);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        answerB.setCompoundDrawables(drawable, null, null, null);

        drawable = getResources().getDrawable(R.mipmap.ic_exam_c_normal);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        answerC.setCompoundDrawables(drawable, null, null, null);

        drawable = getResources().getDrawable(R.mipmap.ic_exam_d_normal);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        answerD.setCompoundDrawables(drawable, null, null, null);

        drawable = getResources().getDrawable(R.mipmap.ic_exam_e_normal);
        drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
        answerE.setCompoundDrawables(drawable, null, null, null);
    }

    private void setCbxsEnable(boolean status) {
        answerA.setEnabled(status);
        answerB.setEnabled(status);
        answerC.setEnabled(status);
        answerD.setEnabled(status);
        answerE.setEnabled(status);
    }

    private void setCbxChecked(String answer) {
        switch (answer) {
            case "A":
                answerA.setChecked(true);
                break;
            case "B":
                answerB.setChecked(true);
                break;
            case "C":
                answerC.setChecked(true);
                break;
            case "D":
                answerD.setChecked(true);
                break;
            case "E":
                answerE.setChecked(true);
                break;
            default:
                break;
        }
    }

    public String getQuestionId() {
        return question.getQuestionId() + "";
    }

    public String getType() {
        return question.getType() + "";
    }

    public String getExamAnswer() {
        return examAnswer;
    }

}
