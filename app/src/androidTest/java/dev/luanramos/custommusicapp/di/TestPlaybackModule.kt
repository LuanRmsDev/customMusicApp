package dev.luanramos.custommusicapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import dev.luanramos.custommusicapp.data.di.PlaybackModule
import dev.luanramos.custommusicapp.data.player.FakeTrackPlaybackController
import dev.luanramos.custommusicapp.domain.TrackPlaybackController
import javax.inject.Singleton

@Module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [PlaybackModule::class]
)
object TestPlaybackModule {

    @Provides
    @Singleton
    fun provideTrackPlaybackController(): TrackPlaybackController = FakeTrackPlaybackController
}
