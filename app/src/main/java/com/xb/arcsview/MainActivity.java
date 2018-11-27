package com.xb.arcsview;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.xb.arcsview.data.Data;
import com.xb.arcsview.view.ArcsView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ArcsView mArcsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mArcsView = findViewById(R.id.av_my);
        initData();
    }

    private void initData() {
        List<Data> datas = new ArrayList<>();
        Data data1 = new Data();
        data1.setAmount(8200);
        data1.setAmountText("余额");
        data1.setColor(Color.parseColor("#FFFFFF"));
        datas.add(data1);

        Data data2 = new Data();
        data2.setAmount(572);
        data2.setColor(Color.parseColor("#0C0D3B"));
        data2.setAmountText("冻结");
        datas.add(data2);

        Data data3 = new Data();
        data3.setAmount(5000);
        data3.setAmountText("消费");
        datas.add(data3);

        Data data4 = new Data();
        data4.setAmount(1200);
        data4.setAmountText("衣服");
        datas.add(data4);

        mArcsView.setData(datas);
    }
}
