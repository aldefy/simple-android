package org.simple.clinic.overdue.communication

import com.f2prateek.rx.preferences2.Preference
import com.f2prateek.rx.preferences2.RxSharedPreferences
import dagger.Module
import dagger.Provides
import org.simple.clinic.AppDatabase
import org.simple.clinic.util.None
import org.simple.clinic.util.Optional
import org.simple.clinic.util.OptionalRxPreferencesConverter
import org.simple.clinic.util.StringPreferenceConverter
import retrofit2.Retrofit
import javax.inject.Named

@Module
class CommunicationModule {

  @Provides
  fun dao(appDatabase: AppDatabase): Communication.RoomDao {
    return appDatabase.communicationDao()
  }

  @Provides
  fun syncApi(retrofit: Retrofit): CommunicationSyncApiV2 {
    return retrofit.create(CommunicationSyncApiV2::class.java)
  }

  @Provides
  @Named("last_communication_pull_token")
  fun lastPullToken(rxSharedPrefs: RxSharedPreferences): Preference<Optional<String>> {
    return rxSharedPrefs.getObject("last_communication_pull_token_v2", None, OptionalRxPreferencesConverter(StringPreferenceConverter()))
  }
}
