package com.wl.pluto.plutochat.chat.fragment;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wl.pluto.plutochat.R;
import com.wl.pluto.plutochat.chat.activity.AdvanceEditGalleyActivity;
import com.wl.pluto.plutochat.chat.activity.PeopleNearbyActivity;

/**
 * DiscoverFragment
 */
public class FrameworkDiscoverFragment extends Fragment {


    /**
     * 朋友圈
     */
    private TextView mMomentsTextView;

    /**
     * 扫描二维码
     */
    private TextView mScanQRCodeTextView;

    /**
     * 摇一摇
     */
    private TextView mShakeTextView;

    /**
     * 附件的人
     */
    private TextView mNearbyPeopleTextView;

    /**
     * 漂流瓶
     */
    private TextView mDriftBottleTextView;

    /**
     * 游戏
     */
    private TextView mGamesTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        final View fragmentLayout = inflater.inflate(R.layout.fragment_framework_discover, container, false);

        initLayout(fragmentLayout);

        return fragmentLayout;
    }


    private void initLayout(final View layout) {

        mMomentsTextView = (TextView) layout.findViewById(R.id.tv_discover_moments);
        mMomentsTextView.setOnClickListener(clickListener);

        mScanQRCodeTextView = (TextView) layout.findViewById(R.id.tv_discover_scan_QR_code);
        mScanQRCodeTextView.setOnClickListener(clickListener);

        mShakeTextView = (TextView) layout.findViewById(R.id.tv_discover_shake);
        mShakeTextView.setOnClickListener(clickListener);

        mNearbyPeopleTextView = (TextView) layout.findViewById(R.id.tv_discover_people_nearby);
        mNearbyPeopleTextView.setOnClickListener(clickListener);

        mDriftBottleTextView = (TextView) layout.findViewById(R.id.tv_discover_drift_bottle);
        mDriftBottleTextView.setOnClickListener(clickListener);

        mGamesTextView = (TextView) layout.findViewById(R.id.tv_discover_games);
        mGamesTextView.setOnClickListener(clickListener);

    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.tv_discover_moments:
                    break;

                case R.id.tv_discover_scan_QR_code:
                    break;

                case R.id.tv_discover_shake:
                    break;

                case R.id.tv_discover_people_nearby:

                    frowardTargetActivity(PeopleNearbyActivity.class);
                    break;

                case R.id.tv_discover_drift_bottle:
                    break;

                case R.id.tv_discover_games:
                    frowardTargetActivity(AdvanceEditGalleyActivity.class);
                    break;
            }
        }
    };

    private void frowardTargetActivity(Class targetActivity) {
        Intent intent = new Intent(getActivity(), targetActivity);
        getActivity().startActivity(intent);
    }
}
