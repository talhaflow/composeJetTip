package com.talhakara.jettip

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.talhakara.jettip.Widgets.RoundIconButton
import com.talhakara.jettip.component.InputField
import com.talhakara.jettip.ui.theme.JetTipTheme
import com.talhakara.jettip.util.calculateTotalPerPerson
import com.talhakara.jettip.util.calculateTotalTip

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainContent()
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit){
    JetTipTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
           content()
        }
    }

}


//@Preview
@Composable
fun TopHeader(totalPerson:Double=0.0){
    Surface(modifier = Modifier
        .fillMaxWidth()
        .height(150.dp)
        .padding(12.dp)
        .clip(shape = RoundedCornerShape(corner = CornerSize(12.dp))),
        color = Color(0xFFE9D7E7) ) {
        Column (modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center){

            var total="%.2f".format(totalPerson)
            Text(text = "total per person",
                style=MaterialTheme.typography.titleLarge)
            Text(text = "$$total",
                style=MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.ExtraBold)



        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Preview
@Composable
fun MainContent(){
    BillForm(){ billAmt ->
        Log.d("AMT", "MainContent: ${billAmt.toInt()*100}")

    }

}



@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BillForm(modifier: Modifier = Modifier,
             onValChange: (String) -> Unit
){

    val totalBillState= remember{
        mutableStateOf("")
    }
    val validState= remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }
    val keyboardController=LocalSoftwareKeyboardController.current

    val sliderPositionState = remember{
        mutableStateOf(0f)
    }
    val tipPercentage=(sliderPositionState.value*100).toInt()

    val splitByState= remember{
        mutableStateOf(1)
    }

    val tipAmountState = remember {
        mutableStateOf(0.0)
    }
    val totalPerPersonState = remember {
        mutableStateOf(0.0)
    }


    Surface (modifier = Modifier
        .padding(2.dp)
        .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(12.dp)),
        border = BorderStroke(width = 1.dp, color = Color.LightGray)
    )
    {
        Column(modifier=Modifier.padding(6.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start) {

            TopHeader(totalPerPersonState.value)
            
            Spacer(modifier = Modifier.height(20.dp))

            InputField(valueState =totalBillState ,
                labelId = "Enter Bill",
                enabled = true,
                isSingleLine =true,
                onAction = KeyboardActions{
                    if (!validState)return@KeyboardActions
                    onValChange(totalBillState.value.trim())

                    keyboardController?.hide() })

            if(validState){

                //split Row
                Row (modifier = Modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.Start){
                    Text(text = "Split",
                        modifier = Modifier.align(
                        alignment = Alignment.CenterVertically
                    ))

                    Spacer(modifier = Modifier.width(120.dp))

                        Row (modifier=Modifier.padding(horizontal = 3.dp),
                            horizontalArrangement = Arrangement.End){

                            RoundIconButton(imageVector =Icons.Default.Remove ,
                                onClick = {
                                    if(splitByState.value>1){
                                    splitByState.value-=1
                                        totalPerPersonState.value=
                                            calculateTotalPerPerson(totalBill =totalBillState.value.toDouble() , splitBy= splitByState.value, tipPercent = tipPercentage)

                                    }else{
                                    splitByState.value=1
                                        totalPerPersonState.value=
                                            calculateTotalPerPerson(totalBill =totalBillState.value.toDouble() , splitBy= splitByState.value, tipPercent = tipPercentage)

                                    }
                                })

                            Text(text = "${splitByState.value}",modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 9.dp, end = 9.dp))

                            RoundIconButton(imageVector =Icons.Default.Add ,
                                onClick = {splitByState.value+=1 })
                        }
                }

                //Tip Row
                Row(modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.End){
                    Text(text = "Tip",modifier=Modifier.align(Alignment.CenterVertically))

                    Spacer(modifier = Modifier.width(200.dp))

                    Text(text = "$${tipAmountState.value}",modifier=Modifier.align(Alignment.CenterVertically))

                }

                //Slider
                Column(verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally){
                    Text(text = "%$tipPercentage")
                    Spacer(modifier = Modifier.height(14.dp))
                    Slider(value =sliderPositionState.value ,
                        onValueChange = { newVal -> sliderPositionState.value=newVal
                            tipAmountState.value=
                                calculateTotalTip(totalBill = totalBillState.value.toDouble(), tipPercent = tipPercentage)

                            totalPerPersonState.value=
                                calculateTotalPerPerson(totalBill =totalBillState.value.toDouble() , splitBy= splitByState.value, tipPercent = tipPercentage)
                                        },
                        modifier=Modifier.padding(start = 16.dp, end = 16.dp),
                        steps = 5,
                        onValueChangeFinished = { Log.d("asd","billfrom finish") }
                        )
                }

            }else{
                Box(){}

            }

        }



    }

}




@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    JetTipTheme {
    }
}