package com.frederis.notsureifreading.view;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.frederis.notsureifreading.R;
import com.frederis.notsureifreading.model.Word;
import com.frederis.notsureifreading.screen.PerformAssessmentScreen;
import com.frederis.notsureifreading.util.LUtils;
import com.frederis.notsureifreading.util.SubscriptionUtil;
import com.frederis.notsureifreading.widget.CheckableFrameLayout;

import java.util.ArrayList;

import javax.inject.Inject;

import mortar.Mortar;
import rx.Observable;

public class PerformAssessmentView extends FractionalViewPager
    implements SubscriptionUtil.PagerDataHandler<ArrayList<Word>> {

    @Inject PerformAssessmentScreen.Presenter mPresenter;

    private Adapter mAdapter;

    public PerformAssessmentView(Context context, AttributeSet attrs) {
        super(context, attrs);

        Mortar.inject(context, this);
    }

    @Override protected void onFinishInflate() {
        super.onFinishInflate();

        mPresenter.takeView(this);
    }

    @Override protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        mPresenter.dropView(this);
    }


    public void showWords(Observable<ArrayList<Word>> words) {
        SubscriptionUtil.subscribeViewPager(words, this);
    }

    public long getStartingWord() {
        return mAdapter.getData().get(0).word.getId();
    }

    public long getEndingWord() {
        ArrayList<Adapter.WordAssessment> data = mAdapter.getData();
        return data.get(data.size() - 1).word.getId();
    }

    public long get1To50Results() {
        ArrayList<Adapter.WordAssessment> data = mAdapter.getData();

        long result = 0L;

        for (int i = 0; i < 50; i++) {
            result = (result << 1) | ((i < data.size() && data.get(i).isCorrect) ? 1 : 0);
        }

        return result;
    }

    public long get51To100Results() {
        ArrayList<Adapter.WordAssessment> data = mAdapter.getData();

        long result = 0L;

        for (int i = 50; i < 100; i++) {
            result = (result << 1) | ((i < data.size() && data.get(i).isCorrect) ? 1 : 0);
        }

        return result;
    }

    public static class Adapter extends PagerAdapter {

        private Context mContext;
        private LUtils mLUtils;
        private ArrayList<WordAssessment> mWordAssessments;

        public Adapter(Context context, ArrayList<WordAssessment> wordAssessments) {
            mContext = context;
            mLUtils = new LUtils(context);
            mWordAssessments = wordAssessments;
        }

        @Override
        public int getCount() {
            return mWordAssessments.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = getView(mContext, container, mWordAssessments.get(position));

            view.setTag(position);

            container.addView(view);

            return view;
        }

        public ArrayList<WordAssessment> getData() {
            return mWordAssessments;
        }

        public View getView(Context context, ViewGroup container, WordAssessment assessment) {
            View view = LayoutInflater.from(context).inflate(R.layout.perform_assessment_page_view, container, false);

            bindView(view, assessment);

            return view;
        }

        public void bindView(View view, final WordAssessment assessment) {
            ((TextView) view.findViewById(R.id.word_name)).setText(assessment.word.getText());

            final CheckableFrameLayout wordIdentifiedButton = (CheckableFrameLayout) view.findViewById(R.id.word_identified_button);
            final ImageView wordIdentifiedIcon = (ImageView) wordIdentifiedButton.findViewById(R.id.word_identified_icon);

            wordIdentifiedButton.setChecked(assessment.isCorrect, false);
            mLUtils.setOrAnimateWordIdentifiedIcon(wordIdentifiedIcon, assessment.isCorrect, false);

            wordIdentifiedButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    boolean isChecked = !wordIdentifiedButton.isChecked();

                    wordIdentifiedButton.setChecked(isChecked, true);
                    mLUtils.setOrAnimateWordIdentifiedIcon(wordIdentifiedIcon, isChecked, true);

                    assessment.isCorrect = isChecked;
                }
            });
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        public static ArrayList<WordAssessment> buildAssessments(ArrayList<Word> words) {
            ArrayList<WordAssessment> assessments = new ArrayList<>();

            for (Word word : words) {
                assessments.add(new WordAssessment(word));
            }

            return assessments;
        }

        public static class WordAssessment {
            Word word;
            boolean isCorrect;

            public WordAssessment(Word word) {
                this(word, false);
            }

            public WordAssessment(Word word, boolean correct) {
                this.word = word;
                this.isCorrect = correct;
            }
        }

    }

    @Override
    public void setData(ArrayList<Word> data) {
        mAdapter = new Adapter(getContext(), Adapter.buildAssessments(data));
        setAdapter(mAdapter);
    }

}
