package hu.bme.aut.tvseries.ui.fragment

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.afollestad.vvalidator.form
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import hu.bme.aut.tvseries.R
import hu.bme.aut.tvseries.api.BackendInteractor
import hu.bme.aut.tvseries.entities.Series
import hu.bme.aut.tvseries.ui.activity.BaseActivity
import hu.bme.aut.tvseries.ui.viewmodel.SharedViewModel
import java.io.File


class NewSeriesFragment : Fragment() {

    private var imageUri: String = ""
    private var imageLandscapeUri: String = ""
    private lateinit var backendInteractor: BackendInteractor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_new_series, container, false)
        backendInteractor = BackendInteractor(requireContext())

        val viewModel: SharedViewModel by activityViewModels()

        val etTitle: TextInputLayout = root.findViewById(R.id.etTitle)
        val etYear: TextInputLayout = root.findViewById(R.id.etYear)
        val etOverview: TextInputLayout = root.findViewById(R.id.etOverview)
        val etPegi: TextInputLayout = root.findViewById(R.id.etPegi)
        val image: ImageView = root.findViewById(R.id.imgCover)
        val imageLandscape: ImageView = root.findViewById(R.id.imgCoverLandscape)
        val btnImage: MaterialButton = root.findViewById(R.id.btnAddImage)
        val btnImageLandscape: MaterialButton = root.findViewById(R.id.btnAddLandscapeImage)
        val btnSave: MaterialButton = root.findViewById(R.id.btnSave)

        val items = resources.getStringArray(R.array.pegi_arrays)
        Log.i("array", items[0])
        val adapter = ArrayAdapter(requireContext(), R.layout.item_dropdown, items)
        (etPegi.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        (etPegi.editText as? AutoCompleteTextView)?.setDropDownBackgroundResource(R.color.colorPrimaryDark)

        btnImage.setOnClickListener { openImagePicker(image, "PORT") }
        btnImageLandscape.setOnClickListener { openImagePicker(imageLandscape, "LAND") }
        form {
            useRealTimeValidation(disableSubmit = true)
            inputLayout(etTitle) {
                isNotEmpty()
            }
            inputLayout(etYear) {
                isNotEmpty()
                isNumber()
            }
            inputLayout(etOverview) {
                isNotEmpty()
            }
            inputLayout(etPegi) {
                isNotEmpty()
            }
        }.submitWith(btnSave) {
            (activity as BaseActivity).showProgressDialog()
            val name = etTitle.editText?.text.toString()
            val year = etYear.editText?.text.toString().toInt()
            val overview = etOverview.editText?.text.toString()
            val pegi = etPegi.editText?.text.toString()
            val series = Series(
                title = name,
                rating = "0",
                ratings = mutableListOf(),
                year = year,
                overview = overview,
                pegi = pegi,
                follow = false,
                cast = mutableListOf(),
                image = imageUri,
                imageLandscape = imageLandscapeUri,
                seasonCount = 0,
                seasons = mutableListOf()
            )
            backendInteractor.postSeries(series, {
                viewModel.setSelected(series)
                (activity as BaseActivity).hideProgressDialog()
                findNavController().popBackStack(R.id.newSeriesFragment, true)
                view?.findNavController()!!.navigate(
                    R.id.action_navigation_home_to_detailsFragment
                )
            }, {
                (activity as BaseActivity).hideProgressDialog()
                Toast.makeText(context, "Create Failed", Toast.LENGTH_SHORT).show()
            })
        }
        return root
    }

    private fun openImagePicker(img: ImageView, type: String) {
        ImagePicker.with(this)
            .compress(1024)            //Final image size will be less than 1 MB(Optional)
            .maxResultSize(
                1080,
                1080
            )    //Final image resolution will be less than 1080 x 1080(Optional)
            .start { resultCode, data ->
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        //Image Uri will not be null for RESULT_OK
                        when (type) {
                            "LAND" -> imageLandscapeUri = data?.data.toString()
                            "PORT" -> imageUri = data?.data.toString()
                        }
                        val fileUri = data?.data
                        img.setImageURI(fileUri)
                        img.scaleType = ImageView.ScaleType.FIT_XY
                        //You can get File object from intent
                        val file: File? = ImagePicker.getFile(data)

                        //You can also get File Path from intent
                        val filePath: String? = ImagePicker.getFilePath(data)
                    }
                    ImagePicker.RESULT_ERROR -> {
                        Toast.makeText(context, ImagePicker.getError(data), Toast.LENGTH_SHORT)
                            .show()
                    }
                    else -> {
                        Toast.makeText(context, "Task Cancelled", Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

}
