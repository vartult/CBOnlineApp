package com.codingblocks.cbonlineapp.adapters

import android.content.Context
import android.graphics.Paint
import android.util.Log
import android.view.View
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.caverock.androidsvg.SVG
import com.codingblocks.cbonlineapp.R
import com.codingblocks.cbonlineapp.activities.CourseActivity
import com.codingblocks.cbonlineapp.database.Course
import com.codingblocks.cbonlineapp.database.CourseWithInstructorDao
import com.codingblocks.cbonlineapp.utils.MediaUtils
import com.codingblocks.cbonlineapp.utils.loadSvg
import com.squareup.picasso.OkHttp3Downloader
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.single_course_card_horizontal.view.*
import okhttp3.OkHttpClient
import org.jetbrains.anko.intentFor
import java.text.SimpleDateFormat
import java.util.*

class AllCoursesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {


    fun bindView(data: Course, courseWithInstructorDao: CourseWithInstructorDao, context: Context) {

        itemView.courseTitle.text = data.title
//            itemView.courseDescription.text = data.subtitle
        itemView.courseRatingTv.text = data.rating.toString()
        itemView.courseRatingBar.rating = data.rating

        //bind Instructors
        val instructorsLiveData = courseWithInstructorDao.getInstructorWithCourseId(data.id)

        instructorsLiveData.observe({ (context as LifecycleOwner).lifecycle }, {
            val instructorsList = it

            var instructors = ""
            for (i in 0 until instructorsList.size) {
                if (i == 0) {
                    Picasso.get().load(instructorsList[i].photo).placeholder(R.drawable.defaultavatar).into(itemView.courseInstrucImgView1)
                    instructors += instructorsList[i].name
                } else if (i == 1) {
                    itemView.courseInstrucImgView2.visibility = View.VISIBLE
                    Picasso.get().load(instructorsList[i].photo).placeholder(R.drawable.defaultavatar).into(itemView.courseInstrucImgView2)
                    instructors += ", ${instructorsList[i].name}"
                } else if (i >= 2) {
                    instructors += "+ " + (instructorsList.size - 2) + " more"
                    break
                }
            }
            if (instructorsList.size < 2) {
                itemView.courseInstrucImgView2.visibility = View.INVISIBLE
            }
            itemView.courseInstructors.text = instructors

        })

//            //bind Runs
        try {
            data.courseRun.run {
                itemView.coursePrice.text = "₹ $crPrice"
                if (crPrice != crMrp && crMrp != "") {
                    itemView.courseActualPrice.text = "₹ $crMrp"
                    itemView.courseActualPrice.paintFlags = itemView.courseActualPrice.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
                }
                val sdf = SimpleDateFormat("MMM dd ")
                var date: String? = ""
                try {
                    date = sdf.format(Date(crStart.toLong() * 1000))
                } catch (nfe: NumberFormatException) {
                    nfe.printStackTrace()
                }
                itemView.courseRun.text = "Batches Starting $date"
                itemView.enrollmentTv.text = "Hurry Up! Enrollment ends $date"
            }
            itemView.courseCoverImgView.loadSvg(data.coverImage)
            itemView.courseLogo.loadSvg(data.logo)

            itemView.setOnClickListener {
                val textPair: Pair<View, String> = Pair(itemView.courseTitle, "textTrans")
                val imagePair: Pair<View, String> = Pair(itemView.courseLogo, "imageTrans")

                //TODO fix transition
//                    val compat = ActivityOptionsCompat.makeSceneTransitionAnimation(context as Activity, textPair, imagePair)
                it.context.startActivity(it.context.intentFor<CourseActivity>("courseId" to data.id, "courseName" to data.title, "courseLogo" to data.logo))

            }
        } catch (e: IndexOutOfBoundsException) {
            error {
                data.promoVideo
            }
        }

    }
}