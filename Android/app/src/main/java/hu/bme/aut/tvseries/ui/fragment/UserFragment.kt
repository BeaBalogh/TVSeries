package hu.bme.aut.tvseries.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import hu.bme.aut.tvseries.R
import hu.bme.aut.tvseries.api.BackendInteractor
import hu.bme.aut.tvseries.entities.Series
import hu.bme.aut.tvseries.ui.activity.BaseActivity
import hu.bme.aut.tvseries.ui.activity.LoginActivity
import hu.bme.aut.tvseries.ui.activity.MainActivity
import hu.bme.aut.tvseries.ui.adapter.SeriesCardAdapter
import hu.bme.aut.tvseries.ui.viewmodel.SharedViewModel
import hu.bme.aut.tvseries.utils.PrefUtils

class UserFragment : Fragment(), SeriesCardAdapter.SeriesItemClickListener {

    private lateinit var viewModel: SharedViewModel
    private lateinit var seriesAdapter: SeriesCardAdapter
    private lateinit var backendInteractor: BackendInteractor

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_user, container, false)
        backendInteractor = BackendInteractor(requireContext())
        loadData()

        //Login
        val btnLogIn: Button = root.findViewById(R.id.btnLogin)

        //UserInfoCard
        val userInfo: ConstraintLayout = root.findViewById(R.id.layoutUserInfo)
        val login: ConstraintLayout = root.findViewById(R.id.layoutLogIn)
        val imgUser: ImageView = root.findViewById(R.id.imgUser)
        val tvName: TextView = root.findViewById(R.id.fullName)
        val tvEmail: TextView = root.findViewById(R.id.email)
        val btnLogOut: Button = root.findViewById(R.id.btnLogOut)
        val activity = activity as MainActivity

        //FollowedCard
        val followedList: RecyclerView = root.findViewById(R.id.followedList)
        val noFollows: TextView = root.findViewById(R.id.noFollows)
        val followedLayoutManager = LinearLayoutManager(
            this.context, RecyclerView.HORIZONTAL, false
        )
        followedList.layoutManager = followedLayoutManager
        seriesAdapter.itemClickListener = this
        followedList.adapter = seriesAdapter

        if (PrefUtils.getUserFollowed(requireContext())?.size ?: 0 == 0) {
            noFollows.visibility = View.VISIBLE
            followedList.visibility = View.GONE
        } else {
            noFollows.visibility = View.GONE
            followedList.visibility = View.VISIBLE
        }


        if (PrefUtils.getUserLoggedIn(requireContext())) {
            userInfo.visibility = View.VISIBLE
            login.visibility = View.GONE
            Glide.with(this)
                .load(activity.userImage)
                .into(imgUser)
            tvName.text = activity.userName
            tvEmail.text = activity.userEmail
        } else {
            userInfo.visibility = View.GONE
            login.visibility = View.VISIBLE
        }

        btnLogOut.setOnClickListener {
            activity.signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
        btnLogIn.setOnClickListener {
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
            startActivity(intent)
        }
        return root
    }

    fun loadData() {
        val list = PrefUtils.getUserFollowed(requireContext()) ?: setOf()
        list.forEach { id ->
            backendInteractor.getSeriesById(id.toLong(), {
                it.follow = true
                viewModel.addFollowed(it)
                seriesAdapter.add(it)
            }, {})
        }
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        seriesAdapter = SeriesCardAdapter()
        val vm: SharedViewModel by activityViewModels()
        viewModel = vm
    }


    override fun onItemClick(series: Series) {
        viewModel.setSelected(series)
        view?.findNavController()!!.navigate(
            R.id.action_navigation_userinfo_to_detailsFragment
        )
    }

    override fun onFollowedClick(series: Series) {
        val list = PrefUtils.getUserFollowed(requireContext())?.toMutableSet() ?: mutableSetOf()
        if (list.contains(series.id.toString())) {
            backendInteractor.deleteID(
                getby = "followed",
                id = series.id,
                onSuccess = {
                    series.follow = !series.follow
                    seriesAdapter.notifyDataSetChanged()
                    viewModel.followClicked(series)
                    list.remove(series.id.toString())
                    PrefUtils.setUserFollowed(list as Set<String>, requireContext())
                },
                onError = {
                    (activity as BaseActivity).hideProgressDialog()
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_LONG).show()
                })
        } else {
            backendInteractor.postID(
                getby = "followed",
                id = series.id,
                onSuccess = {
                    series.follow = !series.follow
                    seriesAdapter.notifyDataSetChanged()
                    viewModel.followClicked(series)
                    list.add(series.id.toString())
                    PrefUtils.setUserFollowed(list as Set<String>, requireContext())

                },
                onError = {
                    (activity as BaseActivity).hideProgressDialog()
                    Toast.makeText(requireContext(), "Error", Toast.LENGTH_LONG).show()
                })
        }


    }
}

