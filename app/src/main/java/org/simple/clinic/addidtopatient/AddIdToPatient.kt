package org.simple.clinic.addidtopatient

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.UUID

sealed class AddIdToPatient : Parcelable {

  @Parcelize
  data class AddBpPassportToPatient(
      val bpPassportCode: UUID,
      val bpPassportShortCode: String
  ) : AddIdToPatient()
}
