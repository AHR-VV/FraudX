package com.fraudx.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fraudx.app.R
import com.fraudx.app.theme.InterFontFamily
import com.fraudx.app.theme.PlusJakartaSansFamily
import com.fraudx.app.theme.VerdantColors

@Composable
fun FraudXTopBar(
    subtitle: String,
    isRootDashboard: Boolean,
    onLeadingClick: () -> Unit,
    subtitleAccentGreen: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(VerdantColors.Surface.copy(alpha = 0.96f))
            .statusBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(
                onClick = onLeadingClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = if (isRootDashboard) {
                        Icons.Outlined.Settings
                    } else {
                        Icons.AutoMirrored.Rounded.ArrowBack
                    },
                    contentDescription = if (isRootDashboard) "Open Settings" else "Navigate Back",
                    tint = VerdantColors.DeepForestInk,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Image(
                painter = painterResource(id = R.drawable.ic_fraudx_shield),
                contentDescription = "FraudX Shield Logo",
                modifier = Modifier.size(width = 24.dp, height = 28.dp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(verticalArrangement = Arrangement.Center) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = VerdantColors.DashboardHeadingInk)) {
                            append("Fraud")
                        }
                        withStyle(SpanStyle(color = VerdantColors.BrandXGreen)) {
                            append("X")
                        }
                    },
                    fontFamily = PlusJakartaSansFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    lineHeight = 20.sp,
                    letterSpacing = (-0.3).sp
                )
                Text(
                    text = if (isRootDashboard) subtitle.uppercase() else subtitle,
                    fontFamily = InterFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    lineHeight = 13.sp,
                    letterSpacing = if (isRootDashboard) 1.2.sp else 0.4.sp,
                    color = if (subtitleAccentGreen) VerdantColors.Primary else VerdantColors.Outline
                )
            }
        }
        HorizontalDivider(
            thickness = 1.dp,
            color = VerdantColors.BorderHairline.copy(alpha = 0.8f)
        )
    }
}
