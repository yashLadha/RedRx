package tech.yashladha.redrx

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.ContactsContract
import android.support.annotation.NonNull
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import android.widget.Toast
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import retrofit2.Retrofit
import tech.yashladha.redrx.Adapter.ImageAdapter
import tech.yashladha.redrx.Models.Contacts
import tech.yashladha.redrx.Models.Country
import tech.yashladha.redrx.Models.WorldPopulation
import tech.yashladha.redrx.Services.ApiService
import java.io.*
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream


class MainActivity : AppCompatActivity() {

    private lateinit var retrofit: Retrofit
    private lateinit var rv: RecyclerView
    private lateinit var countriesList: MutableList<Country>
    private lateinit var rvAdapter: ImageAdapter
    private val PERMISSION_REQUEST = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getPermissions()

        retrofit = RetrofitBuilder(getString(R.string.APIURL)).getBuilder()
        rv = rv_grid

        countriesList = arrayListOf()
        val lm = GridLayoutManager(this, 3)
        rvAdapter = ImageAdapter(countriesList, this)
        rv.layoutManager = lm
        rv.adapter = rvAdapter

        getData()

        fab_contacts.setOnClickListener {
            getContacts()
        }

    }

    private fun getContacts() {
        getContactsList()
                .subscribeOn(Schedulers.io())
                .flatMap({t: MutableList<Contacts> -> generateCSV(t) })
                .flatMap({t: String ->  generateZip(t)})
                .subscribe(
                        { },
                        { err ->
                            error("Error is received" + err.localizedMessage)
                        },
                        {
                            Log.d("Completion Status: ", "Completed")
                            val parentLayout = findViewById<View>(android.R.id.content)
                            val snackbar = Snackbar.make(parentLayout, "Exported ZIP File", Snackbar.LENGTH_LONG)
                            snackbar.setAction("CLOSE", {
                                snackbar.dismiss()
                            }).show()
                        })
    }

    private fun generateZip(filePath: String): Observable<String> {
        try {
            val zipFilePath = File(Environment.getExternalStorageDirectory(), "test.zip")
            ZipOutputStream(BufferedOutputStream(FileOutputStream(zipFilePath.absolutePath))).use { out ->
                val data = ByteArray(1024)
                    FileInputStream(filePath).use { fi ->
                        BufferedInputStream(fi).use { origin ->
                            val entry = ZipEntry(filePath.substring(filePath.lastIndexOf("/")+1))
                            out.putNextEntry(entry)
                            while (true) {
                                val readBytes = origin.read(data)
                                if (readBytes == -1) {
                                    break
                                }
                                out.write(data, 0, readBytes)
                            }
                        }
                    }
                }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val fileObj = File(filePath)
        fileObj.delete()
        return Observable.just("Done")
    }

    private fun generateCSV(contactsList: MutableList<Contacts>) : Observable<String> {
        val file = File(Environment.getExternalStorageDirectory(), "test.csv")
        try {
            if (!file.exists())
                file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        try {
            val writer = FileWriter(file.absolutePath)
            for (contact in contactsList) {
                writer.append(contact.name)
                writer.append(',')
                writer.append(contact.number)
                writer.append("\n")
            }
            writer.flush()
            writer.close()
            return Observable.just(file.absolutePath)
        } catch (e: IOException) {
            error("IO Exeception")
        }
    }

    private fun getContactsList(): Observable<MutableList<Contacts>> {
        return Observable.defer {
            Observable.just(getPhoneContacts())
        }
    }

    private fun getPhoneContacts(): MutableList<Contacts>? {
        val cursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null
        )
        val contactsList : MutableList<Contacts> = mutableListOf()
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val name = cursor.getString(
                        cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                )
                val phoneNumber = cursor.getString(
                        cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                )
                val contact = Contacts(name, phoneNumber)
                contactsList.add(contact)
            }
        }
        cursor.close()
        return if (contactsList.size > 0)
            contactsList
        else
            null
    }

    private fun getPermissions() {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(
                        arrayOf(Manifest.permission.READ_CONTACTS,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE),
                        PERMISSION_REQUEST
                        )
            }


    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            @NonNull permissions: Array<String>,
                                            @NonNull grantResults: IntArray) {
        if (requestCode == PERMISSION_REQUEST) {
            for ( i in 0 until permissions.size) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED ) {
                    Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun getData() {
        getWorldPopulation()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        { countries ->
                            Log.d("Size", countries.countries.size.toString())
                            countriesList.addAll(countries.countries)
                            rvAdapter.notifyDataSetChanged()
                        },
                        {
                            error("Error in subscribe")
                        },
                        {
                            Log.d("Receiver", "Completed")
                        })
    }

    private fun getApiData(): WorldPopulation? {
        val service = retrofit.create(ApiService::class.java)
        val call = service.countries
        try {
            val response = call.execute()
            if (response.isSuccessful) {
                return response.body()
            }
        } catch (e: Exception) {
            error("Unable to download data")
        }
        return null
    }

    private fun getWorldPopulation(): Observable<WorldPopulation> {
        return Observable.defer {
            Observable.just(getApiData())
        }
    }
}
