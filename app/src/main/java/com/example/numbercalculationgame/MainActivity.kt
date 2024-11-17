package com.example.numbercalculationgame

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.Window
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.example.numbercalculationgame.databinding.ActivityMainBinding
import com.example.numbercalculationgame.databinding.DialogResultBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import kotlin.random.Random

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private var isPlayer =  false
    private var firstRandomNumber :Int? = null
    private var secondRandomNumber : Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.apply {

           

            btnStartOrNext.setOnClickListener {

                if(isPlayer){
                    getRandomNumber()
                    tvScore.text =(tvScore.text.toString().toInt()-1).toString()
                }else{

                    isPlayer = true
                    btnStartOrNext.text="Next!"
                    cardQuestion.visibility = View.VISIBLE
                    cardScore.visibility=View.VISIBLE
                    getRandomNumber()
                    runTimer()


                }


            }

            etAnswer.addTextChangedListener {
                val answer = firstRandomNumber!! + secondRandomNumber!!
                if (!it.isNullOrEmpty() && it.toString().toInt() == answer)
                {
                    //answer is true
                    tvScore.text = (tvScore.text.toString().toInt()+1).toString()
                    etAnswer.setText("")
                    getRandomNumber()
                }
            }
        }
    }

    private fun runTimer() {
        lifecycleScope.launch(Dispatchers.IO)
        {
            (1..29).asFlow().onStart {

                binding.constraintLayout.transitionToEnd()

            }.onCompletion {
                //game finished. show dialog to user

                runOnUiThread {
                    binding.cardQuestion.visibility = View.GONE
                    val dialogBinding = DialogResultBinding.inflate(layoutInflater)
                    val dialog = Dialog(this@MainActivity)
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                    dialog.setContentView(dialogBinding.root)
                    dialog.setCancelable(false)
                    dialog.show()
                    dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.WRAP_CONTENT)
                    dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

                    //clicks
                    dialogBinding.apply {
                        //show data in dialog
                        tvDialogScore.text = binding.tvScore.text
                        btnClose.setOnClickListener {
                            dialog.dismiss()
                            finish()
                        }
                        btnTryAgain.setOnClickListener {
                            dialog.dismiss()
                            binding.apply {
                                btnStartOrNext.text = getString(R.string.start_game)
                                cardQuestion.visibility = View.GONE
                                cardScore.visibility = View.GONE
                                isPlayer = false
                                constraintLayout.setTransition(R.id.start,R.id.end)
                                constraintLayout.transitionToEnd()
                                tvScore.text = "0"
                            }
                        }
                    }


                }

            }.collect{ delay(1000) }
        }

    }


    private fun getRandomNumber(){

        firstRandomNumber = Random.nextInt(2,99)
        secondRandomNumber = Random.nextInt(2,99)

        binding.tvQuestionNumber.text = "$firstRandomNumber + $secondRandomNumber"
    }
}