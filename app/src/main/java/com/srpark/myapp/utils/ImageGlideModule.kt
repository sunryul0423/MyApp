package com.srpark.myapp.utils

import android.content.Context
import com.bumptech.glide.Glide
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory
import com.bumptech.glide.load.engine.cache.LruResourceCache
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.module.AppGlideModule
import java.io.InputStream

@GlideModule
class ImageGlideModule : AppGlideModule() {

    companion object {
        const val DISK_CACHE_NAMES = "image_cache"
    }

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.replace(GlideUrl::class.java, InputStream::class.java, OkHttpUrlLoader.Factory())
    }

    override fun applyOptions(context: Context, builder: GlideBuilder) {
        val memoryCacheSize = Runtime.getRuntime().maxMemory() / 1024 / 8
        val diskCacheSizeBytes = (1024 * 1024 * 10).toLong()  //10MB
        builder.setMemoryCache(LruResourceCache(memoryCacheSize))
        builder.setDiskCache(InternalCacheDiskCacheFactory(context, DISK_CACHE_NAMES, diskCacheSizeBytes))
    }


}