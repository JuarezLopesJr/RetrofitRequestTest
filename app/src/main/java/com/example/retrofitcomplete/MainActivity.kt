package com.example.retrofitcomplete

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.retrofitcomplete.databinding.ActivityMainBinding
import retrofit2.HttpException
import java.io.IOException

const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var todoAdapter: TodoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        setUpRecyclerView()
        makeNetworkRequest()
    }

    private fun setUpRecyclerView() = binding.rvTodos.apply {
        todoAdapter = TodoAdapter()
        adapter = todoAdapter

        /* Can also pass the layout manager in the xml file
        layoutManager = LinearLayoutManager(this@MainActivity)
        */
    }

    private fun makeNetworkRequest() {
        lifecycleScope.launchWhenCreated {
            binding.progressBar.isVisible = true

            val response = try {
                TodoService.api.getTodos()

            } catch (e: IOException) {
                binding.progressBar.isVisible = false
                Log.e(TAG, "IOException: no network connection")

                return@launchWhenCreated

            } catch (e: HttpException) {
                binding.progressBar.isVisible = false
                Log.e(TAG, "HttpException: server error")

                return@launchWhenCreated
            }

            if (response.isSuccessful && response.body() != null) {
                todoAdapter.todos = response.body()!!
            } else {
                Log.e(TAG, "Some went wrong")
            }
            binding.progressBar.isVisible = false
        }
    }
}