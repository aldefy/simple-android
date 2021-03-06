package org.simple.clinic.storage.files

import android.app.Application
import com.google.common.truth.Truth.assertThat
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.whenever
import junitparams.JUnitParamsRunner
import junitparams.Parameters
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.IOException

@RunWith(JUnitParamsRunner::class)
class AndroidFileStorageTest {

  private val application = mock<Application>()
  private val fileCreator = mock<FileCreator>()
  private val fileStorage = AndroidFileStorage(application, fileCreator)
  private val applicationFilesDirectory = File("")
  private val file = spy(applicationFilesDirectory.resolve("test.txt"))

  @Before
  fun setUp() {
    whenever(application.filesDir).thenReturn(applicationFilesDirectory)
  }

  @Test
  @Parameters(method = "params for exceptions thrown when getting file")
  fun `exceptions thrown when creating a file must be wrapped in the failure result`(cause: Throwable) {
    whenever(fileCreator.createFileIfItDoesNotExist(any())).thenThrow(cause)

    val result = fileStorage.getFile(filePath = file.path)

    assertThat(result).isEqualTo(GetFileResult.Failure(cause))
  }

  fun `params for exceptions thrown when getting file`(): List<Throwable> {
    return listOf(
        IOException("IO Error!"),
        SecurityException("You shall not pass!"),
        RuntimeException("Oh no, something went wrong!"))
  }

  @Test
  fun `when file is not deleted then throw an exception`() {
    whenever(file.delete()).thenReturn(false)

    val cause = "File: ${file.name} could not be deleted"
    val result = fileStorage.delete(file) as DeleteFileResult.Failure

    assertThat(result.cause.message).isEqualTo(cause)
  }
}
