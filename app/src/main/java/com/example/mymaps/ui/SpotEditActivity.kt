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
import com.example.mymaps.ui.CategoryAdd
import com.example.mymaps.R
import com.example.mymaps.MyApp
import com.example.mymaps.viewmodel.SpotViewModelFactory

class SpotEditActivity : AppCompatActivity() {
    private val spotViewModel: SpotViewModel by viewModels {
        SpotViewModelFactory((application as MyApp).spotRepository)
    }

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

        // 카테고리 단일 선택 옵션
        chipGroupCategories.isSingleSelection = true

        setRequiredLabel(tvPlaceLabel, "장소명")
        setRequiredLabel(tvAddressLabel, "주소")
        setRequiredLabel(tvDescriptionLabel, "설명")

        // intent로 spot 전달
        val spot = intent.getParcelableExtra<SpotEntity>("spot")

        // 기존 값 업데이트
        spot?.let {
            etPlaceName.setText(it.name)
            etAddress.setText(it.roadAddress)
            etSpotDescription.setText(it.description)
            // 이미지
            if (!it.photoUri.isNullOrEmpty()) {
                ivSpotImage.setImageURI(Uri.parse(it.photoUri))
                selectedImageUri = Uri.parse(it.photoUri)
            }
            // 카테고리 chip도 기존 값으로 set
            if (it.categoryEnum != null) {
                // 기본 카테고리 chip 자동 생성 및 체크
                addCategoryChip(it.categoryEnum.displayName, it.categoryPinColorResId)
            } else if (!it.categoryName.isNullOrBlank()) {
                addCategoryChip(it.categoryName, it.categoryPinColorResId)
            }
        }

        /*val placeName = intent.getStringExtra("placeName")
        val placeAddress = intent.getStringExtra("placeAddress")
        val description = intent.getStringExtra("description")

        etPlaceName.setText(placeName)
        etAddress.setText(placeAddress)
        etSpotDescription.setText(description)*/

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
            val photoUri = selectedImageUri?.toString()
            val latitude = spot?.latitude ?: 0.0
            val longitude = spot?.longitude ?: 0.0

            // 카테고리 정보는 chipGroupCategories로 동일
            var pickedCategoryPinColorResId = 0
            var categoryStr = "기타"
            val checkedChipId = chipGroupCategories.checkedChipId
            if (checkedChipId != View.NO_ID) {
                val checkedChip = chipGroupCategories.findViewById<Chip>(checkedChipId)
                categoryStr = checkedChip.text.toString()
                pickedCategoryPinColorResId = checkedChip.tag as? Int ?: 0
            }

            val enumValue = SpotCategory.fromDisplayName(categoryStr)
            val (categoryEnum, customCategory) = if (enumValue != null) {
                enumValue to null
            } else {
                SpotCategory.ETC to categoryStr
            }

            // 기존 spot의 id를 그대로 사용해야 "수정"됨!
            val updatedSpot = spot?.copy(
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

            if (updatedSpot != null) {
                spotViewModel.updateSpot(updatedSpot)
                Toast.makeText(this, "스팟을 수정했어요", Toast.LENGTH_LONG).show()
                finish()
            }
        }

        // 삭제 버튼
        val btnDeleteSpot = findViewById<Button>(R.id.btnDeleteSpot)
        btnDeleteSpot.setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_delete_spot, null)
            val dialog = com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setBackgroundInsetStart(36)
                .setBackgroundInsetEnd(36)
                .create()

            dialogView.findViewById<Button>(R.id.btnCancel).setOnClickListener {
                dialog.dismiss()
            }
            dialogView.findViewById<Button>(R.id.btnDelete).setOnClickListener {
                spot?.let {
                    spotViewModel.deleteSpot(it)
                    Toast.makeText(this, "스팟이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                    finish()
                }
            }
            dialog.show()

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
