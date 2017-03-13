# HoldingButton

Button which is visible while user holds it. Main use case is controlling audio recording state (like in Telegram, Viber, VK). 

<img src="/_arts/example.gif" width="300" height="533"/>

## How to use

1. Wrap your layout with `HoldingButtonLayout`. It is simple `FrameLayout` inside.

```
    <com.dewarder.holdinglibrary.HoldingButtonLayout
        android:id="@+id/input_holder"
        android:layout_width="match_parent"
        android:layout_height="wrap_content">
        
        ...
     
        <ImageView
            android:id="@+id/start_record"
            android:layout_width="48dp"
            android:layout_height="match_parent"
            android:scaleType="center"
            android:src="@drawable/ic_mic_black_24dp"/>

        ...
 
    </com.dewarder.holdinglibrary.HoldingButtonLayout>

```

2. Set `app:hbl_holding_view` property to id of view which would be translated to `HoldingButton`

```
    <com.dewarder.holdinglibrary.HoldingButtonLayout
        android:id="@+id/input_holder"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:hbl_holding_view="@+id/start_record">
```

3. Set `app:hbl_icon` property to icon (usually it is the same as in translated view) which would be appeared in `HoldingButton`.

```
    <com.dewarder.holdinglibrary.HoldingButtonLayout
        android:id="@+id/input_holder"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:hbl_holding_view="@+id/start_record"
        app:hbl_icon="@drawable/ic_mic_black_24dp">
```

4. Enjoy!

## Getting started

Add library as dependency to your build.gradle.

```
compile 'com.dewarder:holdingbutton:0.0.3'
```

## All XML properties

- `hbl_direction`
- `hbl_cancel_offset`
- `hbl_icon` 
- `hbl_cancel_icon`
- `hbl_color`
- `hbl_cancel_color`
- `hbl_second_radius`
- `hbl_second_alpha`
- `hbl_offset_x`
- `hbl_offset_y`
- `hbl_animate_holding_view`

## License

```
Copyright (C) 2017 Artem Glugovsky

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```