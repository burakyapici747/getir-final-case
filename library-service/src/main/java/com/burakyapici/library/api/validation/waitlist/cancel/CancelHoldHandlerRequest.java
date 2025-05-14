package com.burakyapici.library.api.validation.waitlist.cancel;

import com.burakyapici.library.domain.model.User;
import com.burakyapici.library.domain.model.WaitList;

import java.util.UUID;

public record CancelHoldHandlerRequest(
    User patron,
    WaitList waitList,
    UUID waitListId
) {} 