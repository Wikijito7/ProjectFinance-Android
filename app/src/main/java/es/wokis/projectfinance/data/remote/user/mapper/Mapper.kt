package es.wokis.projectfinance.data.remote.user.mapper

import es.wokis.projectfinance.data.bo.user.AuthUserBO
import es.wokis.projectfinance.data.bo.user.BadgeBO
import es.wokis.projectfinance.data.bo.user.ChangePassRequestBO
import es.wokis.projectfinance.data.bo.user.UpdateUserBO
import es.wokis.projectfinance.data.bo.user.UserBO
import es.wokis.projectfinance.data.remote.user.dto.AuthUserDTO
import es.wokis.projectfinance.data.remote.user.dto.BadgeDTO
import es.wokis.projectfinance.data.remote.user.dto.ChangePassRequestDTO
import es.wokis.projectfinance.data.remote.user.dto.UpdateUserDTO
import es.wokis.projectfinance.data.remote.user.dto.UserDTO
import java.util.Date

fun UserDTO.toBO() = UserBO(
    id = id,
    username = username,
    email = email,
    image = image,
    lang = lang,
    totpEnabled = totpEnabled,
    devices = devices,
    createdOn = Date(createdOn),
    emailVerified = emailVerified,
    loginWithGoogle = loginWithGoogle,
    badges = badges.toBO()
)

fun List<BadgeDTO>.toBO() = this.map { it.toBO() }

fun BadgeDTO.toBO() = BadgeBO(
    id = id,
    color = color
)

fun AuthUserBO.toDTO() = AuthUserDTO(
    username = username,
    password = password,
    email = email,
    lang = lang
)

fun UpdateUserBO.toDTO() = UpdateUserDTO(
    username = username,
    email = email
)

fun ChangePassRequestBO.toDTO() = ChangePassRequestDTO(
    oldPass = oldPass,
    recoverCode = recoverCode,
    newPass = newPass
)