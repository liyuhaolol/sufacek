package spa.lyh.cn.sufacek;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import spa.lyh.cn.lib_utils.TextDetailUtils;
import spa.lyh.cn.lib_utils.translucent.TranslucentUtils;
import spa.lyh.cn.lib_utils.translucent.navbar.NavBarFontColorControler;
import spa.lyh.cn.lib_utils.translucent.statusbar.StatusBarFontColorControler;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    TextView tv_show;
    LinearLayout edit_area;
    MyEditText ed_show;
    WebView webView;
    TextView ed_btn;
    ProgressBar progressBar;
    String url;
    String title = "未能获取标题";
    RelativeLayout rl_click;
    ImageView iv_rightImg;
    boolean isStop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TranslucentUtils.setTranslucentBoth(getWindow());
        StatusBarFontColorControler.setStatusBarMode(getWindow(),true);
        NavBarFontColorControler.setNavBarMode(getWindow(),true);
        initView();
        initWebview();
        setWebviewClient();
        setWebChromeClient();
    }

    void initView(){
        rl_click = findViewById(R.id.rl_click);
        rl_click.setOnClickListener(this);
        iv_rightImg = findViewById(R.id.iv_rightImg);
        tv_show = findViewById(R.id.tv_show);
        tv_show.setOnClickListener(this);
        edit_area = findViewById(R.id.edit_area);
        ed_show = findViewById(R.id.ed_show);
        TextDetailUtils.setEditTextInhibitInputSpace(ed_show);
        ed_show.setOnCancelListener(new MyEditText.OnCancel() {
            @Override
            public void onCancel() {
                if (edit_area.getVisibility() == View.VISIBLE && isSoftShowing()){
                    edit_area.setVisibility(View.GONE);
                    ed_btn.setText("取消");
                }
            }
        });
        ed_show.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                /*判断是否是“GO”键*/
                if(actionId == EditorInfo.IME_ACTION_GO){
                    /*隐藏软键盘*/
                    goToUrl(v.getText().toString());
                    return true;
                }
                return false;
            }
        });
        ed_show.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (TextUtils.isEmpty(text)){
                    ed_btn.setText("取消");
                }else {
                    ed_btn.setText("前往");
                }
            }
        });
        progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress(0);
        ed_btn = findViewById(R.id.ed_btn);
        ed_btn.setOnClickListener(this);
    }

    private void initWebview(){
        webView = findViewById(R.id.web);
        webView.setHorizontalScrollBarEnabled(false);//水平不显示
        webView.setVerticalScrollBarEnabled(false); //垂直不显示
        WebSettings webSettings = webView.getSettings();
        //屏蔽图片
        //webSettings.setBlockNetworkImage(true);
        // 缩放
        webSettings.setSupportZoom(true);
        //开启调试
        WebView.setWebContentsDebuggingEnabled(BuildConfig.DEBUG);

        // 自适应屏幕大小
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadWithOverviewMode(true);

        //使用缓存
        webSettings.setAppCacheEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setDatabaseEnabled(true);

        //DOM Storage
        webSettings.setDomStorageEnabled(true);
        webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);

        //启动对js的支持
        webSettings.setJavaScriptEnabled(true);
        //启动Autoplay
        //webSettings.setMediaPlaybackRequiresUserGesture(false);
        //对图片大小适配
        webSettings.setUseWideViewPort(true);
        //对文字大小适配
        webSettings.setLoadWithOverviewMode(true);
        // 判断系统版本是不是5.0或之上
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //让系统不屏蔽混合内容和第三方Cookie
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);
            webSettings.setMixedContentMode(0);//永远允许
        }
    }

    private void setWebviewClient(){
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                url = request.getUrl().toString();
                tv_show.setText(url);
                ed_show.setText(url);
                return super.shouldOverrideUrlLoading(view, request);
            }

            @Override
            public void onPageFinished(WebView view, String mUrl) {
                url = mUrl;
                ed_show.setText(url);
                setRefresh();
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                setStop();
            }
        });
    }

    private void setWebChromeClient(){
        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onReceivedTitle(WebView view, String newTitle) {
                if (!TextUtils.isEmpty(newTitle)){
                    title = newTitle;
                }else {
                    title = "未能获取标题";
                }
                tv_show.setText(title);
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if(newProgress==100){
                    progressBar.setProgress(newProgress);
                    progressBar.setVisibility(View.GONE);//加载完网页进度条消失
                    progressBar.setProgress(0);
                } else{
                    progressBar.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    progressBar.setProgress(newProgress);//设置进度值
                }
            }

            @Override
            public void onReceivedIcon(WebView view, Bitmap icon) {
                super.onReceivedIcon(view, icon);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_show:
                edit_area.setVisibility(View.VISIBLE);
                ed_show.requestFocus();
                if (!TextUtils.isEmpty(url)){
                    ed_show.setText(url);
                }
                TextDetailUtils.openKeybord(MainActivity.this);
                break;
            case R.id.ed_btn:
                String name = ed_btn.getText().toString();
                if (name.equals("取消")){
                    TextDetailUtils.closeKeybord(MainActivity.this);
                    edit_area.setVisibility(View.GONE);
                    ed_btn.setText("取消");
                }else {
                    //前往
                    goToUrl(ed_show.getText().toString());
                }
                break;
            case R.id.rl_click:
                if (isStop){
                    webView.stopLoading();
                    setRefresh();
                }else {
                    if(!TextUtils.isEmpty(url)){
                        webView.reload();
                    }
                }
                break;
        }
    }

    private void setStop(){
        isStop = true;
        iv_rightImg.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.stop,null));
    }

    private void setRefresh(){
        isStop = false;
        iv_rightImg.setBackground(ResourcesCompat.getDrawable(getResources(),R.drawable.refresh,null));
    }

    @Override
    public void onBackPressed() {
        if(edit_area.getVisibility() == View.VISIBLE){
            edit_area.setVisibility(View.GONE);
            ed_btn.setText("取消");
        }else {
            if (webView.canGoBack()){
                webView.goBack();
            }else {
                super.onBackPressed();
            }
        }
    }

    private boolean isSoftShowing() {
        //获取当屏幕内容的高度
        int screenHeight = this.getWindow().getDecorView().getHeight();
        //获取View可见区域的bottom
        Rect rect = new Rect();
        //DecorView即为activity的顶级view
        this.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        //考虑到虚拟导航栏的情况（虚拟导航栏情况下：screenHeight = rect.bottom + 虚拟导航栏高度）
        //选取screenHeight*2/3进行判断
        return screenHeight*2/3 > rect.bottom;
    }

    private void goToUrl(String mUrl){
        TextDetailUtils.closeKeybord(MainActivity.this);
        edit_area.setVisibility(View.GONE);
        ed_btn.setText("取消");
        if (!mUrl.startsWith("http")){
            url = "http://"+mUrl;
        }else {
            url = mUrl;
        }
        tv_show.setText(url);
        webView.loadUrl(url);
    }
}