package com.example.mirela.appAndroid.activities

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.*
import com.example.mirela.appAndroid.POJO.Chocolate
import com.example.mirela.appAndroid.R
import com.example.mirela.appAndroid.service.UpdateIntentService
import com.example.mirela.appAndroid.utils.DatePickerFragment
import com.example.mirela.appAndroid.utils.TimePickerFragment
import java.io.File
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class UpdateActivity : AppCompatActivity(), DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private lateinit var imagePath: String
    private val REQUEST_TAKE_PHOTO = 1
    private val REQUEST_LOAD_PHOTO = 2
    private var id: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_layout)

        val description = intent.getStringExtra("description")
        val date = intent.getLongExtra("data", 0)
        val imagePath = intent.getStringExtra("imagePath")
        id = intent.getLongExtra("id", 0)

        updateUI(description, date, imagePath)
        val doneBtn = findViewById<FloatingActionButton>(R.id.addBtnDone)
        doneBtn.setOnClickListener {
            onUpdateChocolate()
        }
    }

    private fun updateUI(description: String?, dateLong: Long, imagePath: String) {
        val date = Date(dateLong)
        val data = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(date)
        val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(date)

        findViewById<TextView>(R.id.addDate).text = data.toString()
        findViewById<TextView>(R.id.addTime).text = time.toString()
        findViewById<TextView>(R.id.addDescription).text = description
        findViewById<ImageView>(R.id.textImage).setImageBitmap(BitmapFactory.decodeFile(imagePath))
        findViewById<ImageView>(R.id.textImage).tag = imagePath
    }

    fun showDatePickerDialog(v: View) {
        val newFragment = DatePickerFragment()
        newFragment.show(supportFragmentManager, "datePicker")
    }


    fun showTimePikerDialog(v: View) {
        TimePickerFragment().show(supportFragmentManager, "timePicker")
    }

    private fun onUpdateChocolate() {
        try {
            val description = findViewById<EditText>(R.id.addDescription).text.toString()
            val data = findViewById<TextView>(R.id.addDate).text.toString()
            val time = findViewById<TextView>(R.id.addTime).text.toString()
            val imagePath = findViewById<ImageView>(R.id.textImage).tag.toString()
            val date = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.getDefault()).parse("$data $time")

            val intent = Intent(this, UpdateIntentService::class.java)
            val item = Chocolate(id, description, date, imagePath, Date())
            intent.putExtra("item", item)

            setResult(Activity.RESULT_OK, intent)
            startService(intent)

            this.finish()

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "Cannot update chocolate", Toast.LENGTH_LONG).show()
        }

    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply { imagePath = absolutePath }
    }

    fun onTakePhoto(v: View) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            val photoFile: File? = try {
                createImageFile()
            } catch (e: Exception) {
                Log.e("ERORRR", "COULD NOT CREATE THE FILE NAME")
                null
            }
            photoFile?.also {
                val photoURI: Uri = FileProvider.getUriForFile(this, "com.example.mirela.trainingproblema3", it)
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(intent, REQUEST_LOAD_PHOTO)
            }
        }
    }

    fun onLoadPhoto(v: View) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_TAKE_PHOTO
            )
        } else {
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also { intent ->
                startActivityForResult(intent, REQUEST_TAKE_PHOTO)
            }

        }
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val dateText = findViewById<TextView>(R.id.addDate)
        val dateString = "$dayOfMonth-$month-$year"
        dateText.text = dateString
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val timeText = findViewById<TextView>(R.id.addTime)
        val txt = "$hourOfDay:$minute"
        timeText.text = txt

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_LOAD_PHOTO && resultCode == Activity.RESULT_OK) {
            findViewById<ImageView>(R.id.textImage).setImageBitmap(BitmapFactory.decodeFile(imagePath))
            findViewById<ImageView>(R.id.textImage).tag = imagePath
        }
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            val photoUri = data?.data
            imagePath = getPath(photoUri)
//            Log.e("path", imagePath)
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, photoUri)
            findViewById<ImageView>(R.id.textImage).setImageBitmap(bitmap)
            findViewById<ImageView>(R.id.textImage).tag = imagePath
        }
    }

    private fun getPath(data: Uri?): String {
        val filePathColumns = Array(MediaStore.Images.Media.DATA.length) { MediaStore.Images.Media.DATA }
        val cursor = contentResolver.query(data, filePathColumns, null, null, null)
        cursor.moveToFirst()

        val columnIndex = cursor.getColumnIndex(filePathColumns[0])
        val picturePath = cursor.getString(columnIndex)
        cursor.close()
        return picturePath
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == REQUEST_TAKE_PHOTO)
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).also { intent ->
                    startActivityForResult(intent, REQUEST_TAKE_PHOTO)
                }

            } else {
                Toast.makeText(this, "Permision denied", Toast.LENGTH_LONG)
            }
    }
}