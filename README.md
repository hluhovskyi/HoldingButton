# HoldingButton

<a href="http://www.methodscount.com/?lib=com.dewarder%3Aholdingbutton%3A0.0.5"><img src="https://img.shields.io/badge/Size-22 KB-e91e63.svg"/></a> <a href="http://www.methodscount.com/?lib=com.dewarder%3Aholdingbutton%3A0.0.5"><img src="https://img.shields.io/badge/Methods count-core: 249 | deps: 32-e91e63.svg"/></a> [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-HoldingButton-brightgreen.svg?style=flat)](https://android-arsenal.com/details/1/5436)

Button which is visible while user holds it. Main use case is controlling audio recording state (like in Telegram, Viber, VK). 

<img src="/_arts/example.gif" width="300" height="533"/>

## Getting started

Add library as dependency to your `build.gradle`.

```
compile 'com.dewarder:holdingbutton:0.1.3'
```

## How to use

1. Wrap your layout with `HoldingButtonLayout`. It is simple `FrameLayout` inside.

   ```xml
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

   ```xml
    <com.dewarder.holdinglibrary.HoldingButtonLayout
        android:id="@+id/input_holder"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:hbl_holding_view="@+id/start_record">
   ```

3. Set `app:hbl_icon` property to icon (usually it is the same as in translated view) which would be appeared in `HoldingButton`.

   ```xml
    <com.dewarder.holdinglibrary.HoldingButtonLayout
        android:id="@+id/input_holder"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:hbl_holding_view="@+id/start_record"
        app:hbl_icon="@drawable/ic_mic_black_24dp">
   ```

4. Listen events

   ```java
   
   mHoldingButtonLayout.addListener(new SimpleHoldingButtonLayoutListener() {
       @Override
       public void onOffsetChanged(float offset, boolean isCancel) {
           mSlideToCancel.setTranslationX(-mHoldingButtonLayout.getWidth() * offset);
           mSlideToCancel.setAlpha(1 - SLIDE_TO_CANCEL_ALPHA_MULTIPLIER * offset);
       }
   });
   
   ```

5. Enjoy!

   ![Check full example](https://github.com/dewarder/HoldingButton/tree/master/holdingbuttonsample)

## All XML properties

- `hbl_enabled` (`isButtonEnabled/setButtonEnabled`)

   Set enabled or disabled state of button only.

- `hbl_direction` (`getDirection/setDirection`)

   Set direction of sliding. Possible directions are `start` (from right to left) and `end` (from left to right).

   ![](/_arts/hbl_directions.png)

- `hbl_cancel_offset` (`getCancelOffset/setCancelOffset`)

   Set minimum offset for cancel action.

   ![](/_arts/hbl_cancel_offset.png)

- `hbl_icon` (`setIcon`)

   ![](/_arts/hbl_icon.png)

- `hbl_cancel_icon` (`setCancelIcon`)

   ![](/_arts/hbl_cancel_icon.png)

- `hbl_color` (`getColor/setColor`)

   ![](/_arts/hbl_color.png)

- `hbl_cancel_color` (`getCancelColor/CancelColor`)

   ![](/_arts/hbl_cancel_color.png)

- `hbl_radius` (`getRadius/setRadius`)

   ![](/_arts/hbl_radius.png)

- `hbl_second_radius` (`getSecondRadius/setSecondRadius`)

   ![](/_arts/hbl_second_radius.png)

- `hbl_second_alpha` (`getSecondAlpha/setSecondAlpha`)

   ![](/_arts/hbl_second_alpha.png)

- `hbl_offset_x` (`getOffsetX/setOffsetX`)

   ![](/_arts/hbl_offset_x.png)

- `hbl_offset_y` (`getOffsetY/setOffsetY`)

   ![](/_arts/hbl_offset_y.png)

- `hbl_animate_holding_view` (`setAnimateHoldingView/isAnimateHoldingView`)

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
