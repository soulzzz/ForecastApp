package com.soul.mvvmbase.ui.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.soul.mvvmbase.R
import com.soul.mvvmbase.data.bean.DailyWeather
import com.soul.mvvmbase.data.viewmodel.FutureWeatherViewModel
import com.soul.mvvmbase.databinding.FragmentFutureWeatherBinding
import com.soul.mvvmbase.databinding.ItemFutureWeatherBinding
import com.soul.mvvmbase.util.SvgUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

// TODO: Rename parameter arguments, choose names that match



class FutureWeatherFragment : Fragment() {
    private val TAG = javaClass.simpleName
    private val futureWeatherViewModel:FutureWeatherViewModel by inject()
    private lateinit var futureWeatherBinding:FragmentFutureWeatherBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        futureWeatherBinding = FragmentFutureWeatherBinding.inflate(layoutInflater)
        return futureWeatherBinding.root
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        bindUI()
    }

    private fun bindUI() {
        GlobalScope.launch(Dispatchers.Main) {
            val futureWeather = futureWeatherViewModel.futureWeatherList.await()
            val weatherLocation = futureWeatherViewModel.weatherLocation.await()
            futureWeather.observe(viewLifecycleOwner){
                if (it == null) return@observe
                Log.d(TAG, "bindUI: $it")
                futureWeatherBinding.groupLoading.visibility = View.GONE
                updateRecyclerView(it)
            }
            weatherLocation.observe(viewLifecycleOwner){
                if (it == null) return@observe
                Log.d(TAG, "bindUI: weatherLocation")
                updateLocation(it.city)
            }
        }

    }

    private fun updateRecyclerView(listWeather:List<DailyWeather>) {
        val adapter = FutureWeatherListAdapter()
        adapter.submitList(listWeather)
        futureWeatherBinding.recyclerView.apply {
            layoutManager =LinearLayoutManager(this@FutureWeatherFragment.context)
            this.adapter = adapter
        }
    }

    private fun updateLocation(location: String) {
        (activity as? AppCompatActivity)?.supportActionBar?.title = location
        (activity as? AppCompatActivity)?.supportActionBar?.subtitle = "未来七天"
    }

    companion object {
    }
    class FutureWeatherListAdapter : BaseQuickAdapter<DailyWeather, FutureWeatherListAdapter.VH>() {

        // 自定义ViewHolder类
        class VH(
            parent: ViewGroup,
            val binding: ItemFutureWeatherBinding = ItemFutureWeatherBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            ),
        ) : RecyclerView.ViewHolder(binding.root)

        override fun onCreateViewHolder(context: Context, parent: ViewGroup, viewType: Int): VH {
            // 返回一个 ViewHolder
            return VH(parent)
        }

        override fun onBindViewHolder(holder: VH, position: Int, item: DailyWeather?) {
            if(item == null) return
            holder.binding.textViewDate.text = item.fxDate
            holder.binding.textViewCondition.text  = item.textDay
            SvgUtil.updateIcon(this@FutureWeatherListAdapter.context,item.iconDay,holder.binding.imageViewConditionIcon)
            holder.binding.textViewTemperature.text = item.tempMin+"~"+item.tempMax
        }

    }
}