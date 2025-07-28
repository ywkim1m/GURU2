package com.example.mymaps.ui

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.view.ContextThemeWrapper
import android.view.View
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mymaps.model.SpotCategory
import com.example.mymaps.model.SpotEntity
import com.example.mymaps.viewmodel.SpotViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import androidx.activity.viewModels
import com.example.mymaps.CategoryAdd
import com.example.mymaps.R

class SpotEditActivity : AppCompatActivity() {
    private val spotViewModel: SpotViewModel by viewModels()

    private lateinit var btnBack: ImageButton
    private lateinit var ivSpotImage: ImageView
    private lateinit var etPlaceName: EditText
    private lateinit var etAddress: EditText
    private lateinit var etSpotDescription: EditText
    private lateinit var chipGroupCategories: ChipGroup
    private lateinit var btnRegisterSpot: Button
    private lateinit var tvPlaceLabel: TextView
    private lateinit var tvAddressLabel: TextView
    private lateinit var tvDescriptionLabel: TextView
    private lateinit var btnAddCategory: Button
    private lateinit var btnDeleteCategory: Button

    private lateinit var addCategoryLauncher: ActivityResultLauncher<Intent>
    private lateinit var pickImageLauncher: ActivityResultLauncher<PickVisualMediaRequest>
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_spot_edit)

        // Window Insets 적용 (root id = SpotEdit)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.SpotEdit)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnBack = findViewById(R.id.btnBack)
        ivSpotImage = findViewById(R.id.ivSpotImage)
        etPlaceName = findViewById(R.id.etPlaceName)
        etAddress = findViewById(R.id.etAddress)
        etSpotDescription = findViewById(R.id.etSpotDescription)
        chipGroupCategories = findViewById(R.id.chipGroupCategories)
        btnRegisterSpot = findViewById(R.id.btnRegisterSpot)
        tvPlaceLabel = findViewById(R.id.tvPlaceLabel)
        tvAddressLabel = findViewById(R.id.tvAddressLabel)
        tvDescriptionLabel = findViewById(R.id.tvDescriptionLabel)
        btnAddCategory = findViewById(R.id.btnAddCategory)
        btnDeleteCategory = findViewById(R.id.btnDeleteCategory)

        setRequiredLabel(tvPlaceLabel, "장소명")
        setRequiredLabel(tvAddressLabel, "주소")
        setRequiredLabel(tvDescriptionLabel, "설명")

        val latitude = intent.getDoubleExtra("latitude", 0.0)
        val longitude = intent.getDoubleExtra("longitude", 0.0)
        val placeName = intent.getStringExtra("placeName")
        val placeAddress = intent.getStringExtra("placeAddress")

        etPlaceName.setText(placeName)
        etAddress.setText(placeAddress)

        addCategoryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.let { data ->
                    val categoryName = data.getStringExtra("categoryName")
                    val categoryPinColorResId = data.getIntExtra("categoryPinColorResId", 0)
                    if (categoryName != null && categoryPinColorResId != 0) {
                        addCategoryChip(categoryName, categoryPinColorResId)
                    }
                }
            }
        }

        pickImageLauncher = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {
                ivSpotImage.setImageURI(uri)
                selectedImageUri = uri
            }
        }

        // 뒤로가기 버튼
        btnBack.setOnClickListener { finish() }

        // 대표 이미지 클릭
        ivSpotImage.setOnClickListener {
            pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        // 등록(저장) 버튼
        btnRegisterSpot.setOnClickListener {
            val spotTitle = etPlaceName.text.toString()
            val spotDescription = etSpotDescription.text.toString()
            val spotAddress = etAddress.text.toString()
            val latitude = intent.getDoubleExtra("latitude", 0.0)
            val longitude = intent.getDoubleExtra("longitude", 0.0)
            val photoUri = selectedImageUri?.toString()

            // 첫 번째 chip만 대표 카테고리로 저장 (여러개도 선택 가능하면 변경)
            var pickedCategoryPinColorResId = 0
            var categoryStr = "ETC"
            if (chipGroupCategories.childCount > 0) {
                val firstChip = chipGroupCategories.getChildAt(0) as Chip
                categoryStr = firstChip.text.toString()
                pickedCategoryPinColorResId = firstChip.tag as? Int ?: 0
            }
            val (categoryEnum, customCategory) = try {
                SpotCategory.valueOf(categoryStr) to null
            } catch (e: Exception) {
                SpotCategory.ETC to categoryStr
            }

            val spotEntity = SpotEntity(
                name = spotTitle,
                description = spotDescription,
                photoUri = photoUri,
                categoryEnum = categoryEnum,
                categoryName = customCategory,
                categoryPinColorResId = pickedCategoryPinColorResId,
                roadAddress = spotAddress,
                latitude = latitude,
                longitude = longitude,
                isSaved = true
            )

            spotViewModel.insertSpot(spotEntity)
            Toast.makeText(this, "스팟을 추가했어요", Toast.LENGTH_LONG).show()
            finish()
        }

        // 카테고리 추가
        btnAddCategory.setOnClickListener {
            val intent = Intent(this, CategoryAdd::class.java)
            addCategoryLauncher.launch(intent)
        }

        // 카테고리 삭제
        btnDeleteCategory.setOnClickListener {
            val selectedChipIds = chipGroupCategories.checkedChipIds
            val chipsToRemove = mutableListOf<Chip>()

            for (id in selectedChipIds) {
                val chip = findViewById<Chip>(id)
                if (chip != null) chipsToRemove.add(chip)
            }

            var deletedCount = 0
            for (chip in chipsToRemove) {
                chipGroupCategories.removeView(chip)
                deletedCount++
            }

            if (deletedCount > 0) {
                Toast.makeText(this, "${deletedCount}개의 카테고리가 삭제되었습니다", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "삭제할 카테고리를 선택해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 라벨 필수 표시
    private fun setRequiredLabel(textView: TextView, label: String) {
        textView.text = Html.fromHtml("$label <font color='#FF763C'>*</font>", Html.FROM_HTML_MODE_LEGACY)
    }

    // chip 추가
    private fun addCategoryChip(name: String, iconResId: Int) {
        val chip = Chip(ContextThemeWrapper(this, R.style.AppChipStyle))
        chip.text = name
        chip.isCheckable = true
        chip.id = View.generateViewId()

        chip.setEnsureMinTouchTargetSize(false)
        chip.minWidth = 0

        if (iconResId != 0) {
            val drawable: Drawable? = ContextCompat.getDrawable(this, iconResId)
            chip.chipIcon = drawable
            chip.isChipIconVisible = true
        }

        chip.tag = iconResId

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        // chip margin
        layoutParams.marginEnd = resources.getDimensionPixelSize(R.dimen.chip_horizontal_margin)
        chipGroupCategories.addView(chip, layoutParams)
    }
}
