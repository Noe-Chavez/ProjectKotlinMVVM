package com.disoftware.binding

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import javax.inject.Inject

class FragmentBindingAdapters @Inject constructor(
    val fragment: Fragment
) {
    @BindingAdapter("imageUrl")
    fun bindImage(imagenView: ImageView, url: String?) {
        // Cargar la imagen...
        Glide.with(fragment).load(url).into(imagenView) // Cargar cualquier tipo de fragment en nuestra imagen.
    }
}