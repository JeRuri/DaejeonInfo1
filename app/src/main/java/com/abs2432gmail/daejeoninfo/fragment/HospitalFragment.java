package com.abs2432gmail.daejeoninfo.fragment;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.abs2432gmail.daejeoninfo.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.abs2432gmail.daejeoninfo.Common.NetworkConstant.API_KEY;
import static com.abs2432gmail.daejeoninfo.Common.NetworkConstant.HOSPITAL;


public class HospitalFragment extends Fragment {
    private Context mContext = getContext();
    private RecyclerView recyclerView;
    private HosRecyclerViewAdapter adapter;
    private ArrayList<HosRecyclerViewItemData> list = new ArrayList<>();
    private String mTAG = "HospitalFragment";
    private String REQUEST_URL = HOSPITAL + API_KEY;


    public HospitalFragment() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_hospital, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView5);
        recyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        adapter = new HosRecyclerViewAdapter(new ArrayList<HosRecyclerViewItemData>());
        recyclerView.setAdapter(adapter);

        new AsynTaskGetHosData().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        return view;
    }

    public class HosRecyclerViewItemData{
        public String hospitalName, hospitalAdr, hospitalTel;

        public HosRecyclerViewItemData(){}


        public HosRecyclerViewItemData(String hospitalName, String hospitalAdr, String hospitalTel){
            this.hospitalName = hospitalName;
            this.hospitalAdr = hospitalAdr;
            this.hospitalTel = hospitalTel;
        }
    }

    private class HosRecyclerViewAdapter extends RecyclerView.Adapter<HosRecyclerViewAdapter.ViewHolder>{
        private ArrayList<HosRecyclerViewItemData> hosRecyclerViewItemDatas;

        public HosRecyclerViewAdapter(ArrayList<HosRecyclerViewItemData> data) {
            hosRecyclerViewItemDatas = data;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            HosRecyclerViewItemData hosRecyclerViewItemData;
            TextView textView1, textView2, textView3;

            public ViewHolder (View itemView) {
                super(itemView);
                textView1 = itemView.findViewById(R.id.hospitalName);
                textView2 = itemView.findViewById(R.id.hospitalAdr);
                textView3 = itemView.findViewById(R.id.hospitalTel);
            }


            //여기서 사용
            public void setListData(HosRecyclerViewItemData data) {
                this.hosRecyclerViewItemData = data;
                textView1.setText(hosRecyclerViewItemData.hospitalName);
                textView2.setText(hosRecyclerViewItemData.hospitalAdr);
                textView3.setText(hosRecyclerViewItemData.hospitalTel);

            }
        }

        @NonNull
        @Override
        public HosRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hospital_item, parent, false);
            HosRecyclerViewAdapter.ViewHolder viewHolder = new HosRecyclerViewAdapter.ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull HosRecyclerViewAdapter.ViewHolder holder, int position) {
            holder.setListData(hosRecyclerViewItemDatas.get(position));

        }

        @Override
        public int getItemCount() {
            return hosRecyclerViewItemDatas.size();
        }

        public void add (HosRecyclerViewItemData hosRecyclerViewItemData){
            hosRecyclerViewItemDatas.add(hosRecyclerViewItemData);
            notifyDataSetChanged();
        }
    }

    private class AsynTaskGetHosData extends AsyncTask<Void, Integer, ArrayList<HosRecyclerViewItemData>> {
        private String TaskName = "AsynTaskGetHosData";

        //시작전
        protected void onPreExecute() {super.onPreExecute();}


        //완료후
        protected void onPostExecute(ArrayList<HosRecyclerViewItemData> hosRecyclerViewItemDatas) {
            super.onPostExecute(hosRecyclerViewItemDatas);
            if(hosRecyclerViewItemDatas == null)
                return;
            if(hosRecyclerViewItemDatas.size() == 0)
                return;
            adapter = new HosRecyclerViewAdapter(hosRecyclerViewItemDatas);
            recyclerView.setAdapter(adapter);
        }

        @Override
        protected ArrayList<HosRecyclerViewItemData> doInBackground(Void... voids) {
            Boolean flag = null;
            String responseBody = "false";
            ArrayList<HosRecyclerViewItemData> hosRecyclerViewItemDatas = new ArrayList<>();

            try{
                OkHttpClient toServer = new OkHttpClient.Builder()
                        .connectTimeout(60, TimeUnit.SECONDS)
                        .readTimeout(60, TimeUnit.SECONDS)
                        .build();

                RequestBody requestBody = new FormBody.Builder()
                        .build();

                Request request = new Request.Builder()
                        .url(REQUEST_URL)
                        .get()
                        .build();

                Response response = toServer.newCall(request).execute();
                flag = response.isSuccessful();

                int responseCode = response.code();

                if(flag){
                    responseBody = String.valueOf(response.body().string());
                }

                if(responseBody.equals("false")) {
                    Log.d(mTAG, TaskName + " : 서버 연결 실패");
                    return null;
                }

                JSONObject jsonObject = new JSONObject(responseBody);
                JSONArray resultList = jsonObject.getJSONArray("resultList");

                int resultSize = resultList.length();
                if(resultSize == 0 ){
                    Log.d(mTAG, TaskName + " : 정보가 없습니다.");
                    return null;
                }

                for(int i = 0; i < resultSize; i++){
                    JSONObject data = resultList.getJSONObject(i);
                    String HospitalName = data.getString("nm");
                    String HospitalAdr = data.getString("addr1");
                    String HospitalTel = data.getString("phone");
                    HosRecyclerViewItemData hosRecyclerViewItemData = new HosRecyclerViewItemData(HospitalName, HospitalAdr, HospitalTel);
                    hosRecyclerViewItemDatas.add(hosRecyclerViewItemData);
                }

            } catch (Exception e){
                Log.d(mTAG, TaskName+" : "+e.toString());
                return null;
            }

            return hosRecyclerViewItemDatas;
        }

        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
        }
    }

}
