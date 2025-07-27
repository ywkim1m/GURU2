package com.example.mymaps

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Html
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import android.view.Gravity
import android.view.View
import android.net.Uri
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.viewModels
import com.example.mymaps.model.SpotCategory
import com.example.mymaps.model.SpotEntity
import com.example.mymaps.viewmodel.SpotViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SpotDetailActivity : AppCompatActivity() {
    private val spotViewModel: SpotViewModel by viewModels()

    private lateinit var btnBack: ImageButton
    private lateinit var ivSpotImage: ImageView
    private lateinit var etPlaceName: EditText
    private lateinit var etPlaceAddress: EditText
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
        setContentView(R.layout.activity_spot_detail)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.SpotRegist)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btnBack = findViewById(R.id.btnBack)
        ivSpotImage = findViewById(R.id.ivSpotImage)
        etPlaceName = findViewById(R.id.etPlaceName)
        etPlaceAddress = findViewById(R.id.etAddress)
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
        etPlaceAddress.setText(placeAddress)

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


        // 뒤로가기 버튼 클릭 리스너
        btnBack.setOnClickListener {
            finish()
        }

        // 이미지 뷰 클릭 리스너
        ivSpotImage.setOnClickListener {
            pickImageLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }


        // '등록하기' 버튼 클릭 리스너, DB에 저장
        btnRegisterSpot.setOnClickListener {
            val spotTitle = etPlaceName.text.toString() // 장소명
            val spotDescription = etSpotDescription.text.toString() // 장소 설명
            val spotAddress = etPlaceAddress.text.toString()    // 도로명 주소
            val latitude = intent.getDoubleExtra("latitude", 0.0)   // 위도
            val longitude = intent.getDoubleExtra("longitude", 0.0) // 경도
            val photoUri = selectedImageUri?.toString() // 장소 사진

            // 카테고리(Chip에서 첫 번째 선택값 Enum 변환)
            val allCategories = mutableListOf<String>()
            for (i in 0 until chipGroupCategories.childCount) {
                val chip = chipGroupCategories.getChildAt(i) as Chip
                allCategories.add(chip.text.toString())
            }
            val categoryStr = if (allCategories.isNotEmpty()) allCategories[0] else "ETC"
            val spotCategory = try {
                SpotCategory.valueOf(categoryStr)
            } catch (e: Exception) {
                SpotCategory.ETC
            }

            val spotEntity = SpotEntity(
                name = spotTitle,
                description = spotDescription,
                photoUri = photoUri,
                category = spotCategory,
                roadAddress = spotAddress,
                latitude = latitude,
                longitude = longitude,
                isSaved = true
            )

            spotViewModel.insertSpot(spotEntity)
            Toast.makeText(this, "스팟을 추가했어요", Toast.LENGTH_LONG).show()
            finish()
        }

        // '추가' 버튼 (btnAddCategory) 클릭 리스너
        btnAddCategory.setOnClickListener {
            val intent = Intent(this, CategoryAdd::class.java)
            addCategoryLauncher.launch(intent)
        }

        // '삭제' 버튼 (btnDeleteCategory) 클릭 리스너
        btnDeleteCategory.setOnClickListener {
            val selectedChipIds = chipGroupCategories.checkedChipIds
            val chipsToRemove = mutableListOf<Chip>()

            for (id in selectedChipIds) {
                val chip = findViewById<Chip>(id)
                if (chip != null) {
                    chipsToRemove.add(chip)
                }
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

    private fun setRequiredLabel(textView: TextView, label: String) {
        textView.text = Html.fromHtml("$label <font color='#FF763C'>*</font>", Html.FROM_HTML_MODE_LEGACY)
    }

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

        chip.setOnCheckedChangeListener { chipView, isChecked ->
        }

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        layoutParams.marginEnd = resources.getDimensionPixelSize(R.dimen.chip_horizontal_margin)
        chipGroupCategories.addView(chip, layoutParams)
    }
}