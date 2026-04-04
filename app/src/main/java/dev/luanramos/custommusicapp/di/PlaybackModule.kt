package dev.luanramos.custommusicapp.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.luanramos.custommusicapp.data.player.ExoTrackPlaybackController
import dev.luanramos.custommusicapp.domain.TrackPlaybackController
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PlaybackModule {

    @Binds
    @Singleton
    abstract fun bindTrackPlaybackController(
        impl: ExoTrackPlaybackController
    ): TrackPlaybackController
}
