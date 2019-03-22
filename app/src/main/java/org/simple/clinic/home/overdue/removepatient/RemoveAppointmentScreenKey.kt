package org.simple.clinic.home.overdue.removepatient

import kotlinx.android.parcel.IgnoredOnParcel
import kotlinx.android.parcel.Parcelize
import org.simple.clinic.R
import org.simple.clinic.router.screen.FullScreenKey
import java.util.UUID

@Parcelize
class RemoveAppointmentScreenKey(
    val appointmentUuid: UUID,
    val patientUuid: UUID
) : FullScreenKey {

  @IgnoredOnParcel
  override val analyticsName = "RemoveAppointment"

  override fun layoutRes(): Int = R.layout.screen_remove_appointment
}
