package org.simple.clinic.protocolv2.sync

import android.support.test.runner.AndroidJUnit4
import com.f2prateek.rx.preferences2.Preference
import com.google.common.truth.Truth.assertThat
import io.reactivex.Single
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.simple.clinic.AppDatabase
import org.simple.clinic.AuthenticationRule
import org.simple.clinic.TestClinicApp
import org.simple.clinic.protocol.ProtocolConfig
import org.simple.clinic.protocol.ProtocolRepository
import org.simple.clinic.protocol.sync.ProtocolSync
import org.simple.clinic.util.None
import org.simple.clinic.util.Optional
import javax.inject.Inject
import javax.inject.Named

@RunWith(AndroidJUnit4::class)
class ProtocolSyncAndroidTest {

  @Inject
  lateinit var repository: ProtocolRepository

  @Inject
  lateinit var sync: ProtocolSync

  @Inject
  lateinit var appDatabase: AppDatabase

  @Inject
  @field:Named("last_protocol_pull_token")
  lateinit var lastPullToken: Preference<Optional<String>>

  @get:Rule
  val authenticationRule = AuthenticationRule()

  @Inject
  lateinit var config: Single<ProtocolConfig>

  @Before
  fun setUp() {
    TestClinicApp.appComponent().inject(this)
  }

  @Test
  fun when_pulling_protocols_from_server_then_paginate_till_server_has_no_records_anymore() {
    val config = config.blockingGet()
    if(config.isProtocolDrugSyncEnabled.not()){
      return
    }

    lastPullToken.set(None)

    sync.pull()
        .test()
        .assertNoErrors()

    val count = appDatabase.protocolDao().count().blockingFirst()
    assertThat(count).isAtLeast(1)
  }
}