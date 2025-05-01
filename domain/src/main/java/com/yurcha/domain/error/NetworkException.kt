package com.yurcha.domain.error

import com.yurcha.domain.model.StatusCode
import java.io.IOException

class NetworkException(val code: StatusCode) : IOException()
