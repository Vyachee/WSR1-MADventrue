package com.grinvald.grinvaldmadventure

import android.content.Context
import android.content.Context.SENSOR_SERVICE
import android.hardware.Sensor
import android.hardware.Sensor.TYPE_ACCELEROMETER
import android.hardware.Sensor.TYPE_MAGNETIC_FIELD
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.graphics.rotationMatrix
import java.lang.Math.round


class Compass : Fragment(), SensorEventListener {

    lateinit var iv_arrow : ImageView
    var sCompass : Sensor? = null

    lateinit var sensorManager: SensorManager
    lateinit var accelerometer: Sensor
    lateinit var magnetometer: Sensor

    var currentDegree = 0.0f
    var lastAccelerometer = FloatArray(3)
    var lastMagnetometer = FloatArray(3)
    var lastAccelerometerSet = false
    var lastMagnetometerSet = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_compass, container, false)

        iv_arrow = v.findViewById(R.id.iv_arrow)

        sensorManager = requireContext().getSystemService(SENSOR_SERVICE) as SensorManager
        sCompass = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        sensorManager.registerListener(this, sCompass, SensorManager.SENSOR_DELAY_NORMAL)

        accelerometer = sensorManager.getDefaultSensor(TYPE_ACCELEROMETER)
        magnetometer = sensorManager.getDefaultSensor(TYPE_MAGNETIC_FIELD)


        return v
    }

    override fun onResume() {
        super.onResume()


    }


    override fun onSensorChanged(event: SensorEvent?) {
        val value = event!!.values[0]

        SensorManager.getRotationMatrix(FloatArray(9), null, FloatArray(3), FloatArray(9))

        val orientation = SensorManager.getOrientation(FloatArray(9), FloatArray(3))
        val degrees = (Math.toDegrees(orientation.get(0).toDouble()) + 360.0) % 360.0
        val angle = round(degrees * 100) / 100

        iv_arrow.rotation = angle.toFloat()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d("DEBUG", "asd")
    }

}