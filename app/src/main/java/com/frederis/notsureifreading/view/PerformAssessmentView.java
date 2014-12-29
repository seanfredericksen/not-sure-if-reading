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

public class PerformAssessmentView extends ViewPager
    implements SubscriptionUtil.PagerDataHandler<ArrayList<Word>> {

    @Inject PerformAssessmentScreen.Presenter mPresenter;

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

    public static class Adapter extends PagerAdapter {

        private Context mContext;
        private LUtils mLUtils;
        private ArrayList<Word> mWords;

        public Adapter(Context context, ArrayList<Word> words) {
            mContext = context;
            mLUtils = new LUtils(context);
            mWords = words;
        }

        @Override
        public int getCount() {
            return mWords.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = getView(mContext, container, mWords.get(position));

            view.setTag(position);

            container.addView(view);

            return view;
        }

        public View getView(Context context, ViewGroup container, Word word) {
            View view = LayoutInflater.from(context).inflate(R.layout.perform_assessment_page_view, container, false);

            bindView(view, word);

            return view;
        }

        public void bindView(View view, Word word) {
            ((TextView) view.findViewById(R.id.word_name)).setText(word.getText());

            final CheckableFrameLayout wordIdentifiedButton = (CheckableFrameLayout) view.findViewById(R.id.word_identified_button);
            final ImageView wordIdentifiedIcon = (ImageView) wordIdentifiedButton.findViewById(R.id.word_identified_icon);

            wordIdentifiedButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    wordIdentifiedButton.setChecked(true, true);
                    mLUtils.setOrAnimateWordIdentifiedIcon(wordIdentifiedIcon, true, true);
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


    }

    @Override
    public void setData(ArrayList<Word> data) {
        setAdapter(new Adapter(getContext(), data));
    }

}
