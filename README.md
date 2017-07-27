# SideBar
仿微信通讯录页面侧边栏索引控件

![效果动图](https://github.com/laxian/SideBar/blob/master/library/sidebar.gif)

## 使用方法


布局引用
```    <com.zhouweixian.sidebar.SideBar
           android:id="@+id/sidebar"
           android:layout_width="match_parent"
           android:layout_height="match_parent"
           android:layout_alignParentEnd="true"
           android:layout_alignParentRight="true"
           android:layout_alignParentTop="true"
           app:choice_background_color="#99000000"
           app:choice_background_corner="50"
           app:choice_background_height="300"
           app:choice_background_width="300"
           app:choice_font_color="#fff"
           app:choice_font_size="150"
           app:index_background_color_touch="#ccb2b2b2"
           app:index_font_color="@color/colorAccent"
           app:index_font_size="30"
           app:index_touch_width="50" />
```
           

回调
```sideBar.setCurrentIndexCallback(new OnCurrentIndexCallback {
    
    override fun onCurrentIndex(pos: Int, chr: Char) {
            val index = bi_search(list, 0, list.size - 1, chr)
            linearLayoutManager.scrollToPositionWithOffset(index, 0)
        }
    
    })
```