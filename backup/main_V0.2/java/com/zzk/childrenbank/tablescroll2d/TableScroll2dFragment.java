package com.zzk.childrenbank.tablescroll2d;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.zzk.childrenbank.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TableScroll2dFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TableScroll2dFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextView textViewLeftTopTitle1;
    TextView textViewLeftTopTitle2;
    RecyclerView recyclerViewTopTitle;
    RecyclerView recyclerViewTableContent;
    TableRowAdapter tableTopAdapter;
    TableContentAdapter tableContentAdapter;

    String title1, title2;
    int cellWidth, cellHeight;
    int firstRowHeight, firstColumnWidth;
    String[][] data;

    public TableScroll2dFragment() {
        // Required empty public constructor
        title1 = "列标题";
        title2 = "行标题";
        cellWidth = 120;
        cellHeight = 70;
        firstRowHeight = 70;
        firstColumnWidth = 120;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TableScroll2dFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TableScroll2dFragment newInstance(String param1, String param2) {
        TableScroll2dFragment fragment = new TableScroll2dFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragment = inflater.inflate(R.layout.fragment_table_scroll2d, container, false);
        textViewLeftTopTitle1 = fragment.findViewById(R.id.tv_left_title_top);
        textViewLeftTopTitle2 = fragment.findViewById(R.id.tv_left_title_bottom);

        // 设置第一行高度
        LinearLayout linearLayoutTopRoot = fragment.findViewById(R.id.ll_top_root);
        ViewGroup.LayoutParams params = linearLayoutTopRoot.getLayoutParams();
        params.height = firstRowHeight;
        linearLayoutTopRoot.setLayoutParams(params);

        //  设置第一行第一列宽度，第一列下面的单元格宽度在创建各行时设置
        ConstraintLayout constraintLayoutTopLeft = fragment.findViewById(R.id.layout_top_left);
        params = constraintLayoutTopLeft.getLayoutParams();
        params.width = firstColumnWidth;
        constraintLayoutTopLeft.setLayoutParams(params);

        //  左上单元格分隔线旋转
        View sperateLine = fragment.findViewById(R.id.tv_left_title_line);
        double angle = Math.atan((double) firstRowHeight/firstColumnWidth)*180/Math.PI;
        sperateLine.setRotation((float)angle);

        textViewLeftTopTitle1.setText(title1);
        textViewLeftTopTitle2.setText(title2);

        recyclerViewTopTitle = fragment.findViewById(R.id.recycler_title);
        LinearLayoutManager layoutManagerTopTitle = new LinearLayoutManager(getContext());
        layoutManagerTopTitle.setOrientation(RecyclerView.HORIZONTAL);
        recyclerViewTopTitle.setLayoutManager(layoutManagerTopTitle);

        recyclerViewTableContent = fragment.findViewById(R.id.recycler_content);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerViewTableContent.setLayoutManager(layoutManager);

        tableTopAdapter = new TableRowAdapter();
        tableContentAdapter = new TableContentAdapter();

        tableTopAdapter.setCellSize(cellWidth, cellHeight);
        tableContentAdapter.setCellSize(cellWidth, cellHeight);
        tableContentAdapter.setFirstColumnWidth(firstColumnWidth);

        tableContentAdapter.initRowRecyclerView(recyclerViewTopTitle, getContext());

        if(data!=null) {
            tableTopAdapter.setTitles(data[0]);
            tableContentAdapter.setData(data);
        }

        recyclerViewTopTitle.setAdapter(tableTopAdapter);
        recyclerViewTableContent.setAdapter(tableContentAdapter);
        return fragment;
    }

    public void setLeftTopTitle(String title1, String title2) {
        this.title1 = title1;
        this.title2 = title2;
    }

    public void setCellSize(int width, int height) {
        this.cellWidth = width;
        this.cellHeight = height;
    }

    public void setFirstCellSize(int firstColumnWidth, int firstRowHeight){
        this.firstRowHeight = firstRowHeight;
        this.firstColumnWidth = firstColumnWidth;
    }

    public void setData(String[][] data) {
        this.data = data;
        if(tableTopAdapter!=null) {
            tableTopAdapter.setTitles(data[0]);
            tableTopAdapter.notifyDataSetChanged();
        }
        if(tableContentAdapter!=null) {
            tableContentAdapter.setData(data);
            tableContentAdapter.notifyDataSetChanged();
        }
    }
}




