package org.simple.clinic.home.overdue.removepatient

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioButton
import com.jakewharton.rxbinding2.view.RxView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers.io
import kotterknife.bindView
import org.simple.clinic.R
import org.simple.clinic.activity.TheActivity
import org.simple.clinic.overdue.AppointmentCancelReason
import org.simple.clinic.overdue.AppointmentCancelReason.InvalidPhoneNumber
import org.simple.clinic.overdue.AppointmentCancelReason.MovedToPrivatePractitioner
import org.simple.clinic.overdue.AppointmentCancelReason.Other
import org.simple.clinic.overdue.AppointmentCancelReason.PatientNotResponding
import org.simple.clinic.overdue.AppointmentCancelReason.TransferredToAnotherPublicHospital
import org.simple.clinic.router.screen.ScreenRouter
import org.simple.clinic.widgets.ScreenDestroyed
import org.simple.clinic.widgets.UiEvent
import javax.inject.Inject

class RemoveAppointmentScreen(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

  @Inject
  lateinit var controller: RemoveAppointmentScreenController

  @Inject
  lateinit var screenRouter: ScreenRouter

  private val patientAlreadyVisitedRadioButton by bindView<RadioButton>(R.id.removeappointment_reason_patient_already_visited)
  private val notRespondingRadioButton by bindView<RadioButton>(R.id.removeappointment_reason_patient_not_responding)
  private val invalidPhoneNumberRadioButton by bindView<RadioButton>(R.id.removeappointment_reason_invalid_phone_number)
  private val publicHospitalTransferRadioButton by bindView<RadioButton>(R.id.removeappointment_reason_public_hospital_transfer)
  private val movedToPrivateRadioButton by bindView<RadioButton>(R.id.removeappointment_reason_moved_to_private)
  private val diedRadioButton by bindView<RadioButton>(R.id.removeappointment_reason_patient_died)
  private val otherReasonRadioButton by bindView<RadioButton>(R.id.removeappointment_reason_other)
  private val reasonSelectedDoneButton by bindView<View>(R.id.removeappointment_done_button)
  private val closeButton by bindView<View>(R.id.removeappointment_close_button)

  private val screenKey by lazy {
    screenRouter.key<RemoveAppointmentScreenKey>(this)
  }

  @SuppressLint("CheckResult")
  override fun onFinishInflate() {
    super.onFinishInflate()
    if (isInEditMode) {
      return
    }
    TheActivity.component.inject(this)

    val screenDestroys = RxView.detaches(this).map { ScreenDestroyed() }

    Observable.mergeArray(
        sheetCreates(),
        screenDestroys,
        cancelReasonClicks(),
        doneClicks(),
        patientDiedClicks(),
        patientAlreadyVisitedClicks()
    )
        .observeOn(io())
        .compose(controller)
        .observeOn(mainThread())
        .takeUntil(screenDestroys)
        .subscribe { uiChange -> uiChange(this) }

    closeButton.setOnClickListener { closeScreen() }
  }

  private fun sheetCreates(): Observable<UiEvent> =
      Observable.just(RemoveAppointmentSheetCreated(appointmentUuid = screenKey.appointmentUuid))

  private fun doneClicks() =
      RxView
          .clicks(reasonSelectedDoneButton)
          .map { RemoveReasonDoneClicked }

  private fun patientDiedClicks() =
      RxView
          .clicks(diedRadioButton)
          .map { PatientDeadClicked(patientUuid = screenKey.patientUuid) }

  private fun patientAlreadyVisitedClicks() =
      RxView
          .clicks(patientAlreadyVisitedRadioButton)
          .map { PatientAlreadyVisitedClicked }

  private fun cancelReasonClicks(): Observable<UiEvent> {
    val buttonToCancelReasons = mapOf(
        notRespondingRadioButton to PatientNotResponding,
        invalidPhoneNumberRadioButton to InvalidPhoneNumber,
        publicHospitalTransferRadioButton to TransferredToAnotherPublicHospital,
        movedToPrivateRadioButton to MovedToPrivatePractitioner,
        otherReasonRadioButton to Other)

    val reasonClicks = { entry: Map.Entry<View, AppointmentCancelReason> ->
      RxView.clicks(entry.key).map {
        CancelReasonClicked(entry.value)
      }
    }

    return Observable.merge(buttonToCancelReasons.map(reasonClicks))
  }

  fun closeScreen() {
    screenRouter.pop()
  }

  fun enableDoneButton() {
    reasonSelectedDoneButton.isEnabled = true
  }
}
